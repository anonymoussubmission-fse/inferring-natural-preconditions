import os
import traceback
import shutil
import sys
import json
import re
import data_collection_utils as utils
import data_collection_consts as const
import data_collection_gen_inputs as gen_inputs
import data_collection_compile as compile
import data_collection_exception_wrapper as wrapper
import data_collection_reducer as reducer
import data_collection_executor as executor
from args import args
import subprocess as sp
from glob import glob
from multiprocessing import current_process, Pool
from Sample import Sample


def run_main(sample):
    # replace stdout
    proc = current_process()
    idx = proc._identity[0]
    stdout_file = f"../logs/output_{sample.proj_name}_{idx}.txt"
    fio = open(stdout_file, "a")

    sys.stdout = fio
    sys.stderr = fio

    print("\n\n" + "-"*100)

    try:

        if (not args.only_reduce) and utils.data_point_exists(sample):
            print("Data point already exists, skipping")
            return

        if not args.dont_gen_inputs:

            utils.setup(sample)

            '''
                Step 1: Compile the template
            '''
            #if not compile.compile_target(sample, os.path.dirname(sample.full_path)): return
            if not compile.compile_target(sample, sample.proj_dir): return


            '''
                Step 2: Package the target class file into a jar
            '''

            if not compile.package_target(sample, os.path.join(const.BASE_DIR, sample.proj_name)): return

            '''
                Step 3: Run EvoSuite
            '''


            evo_success = gen_inputs.run_evo(sample, sample.newjar)

            if (not evo_success) or gen_inputs.check_for_empty_tests(sample): 
                return

            if (not gen_inputs.clean_evo_tests(sample)) or gen_inputs.check_for_empty_tests(sample): return

            if not gen_inputs.compile_evo_tests(sample, sample.newjar): return

        elif not args.only_reduce:
            print("Using existing evosuite tests for sample", sample.sample_num)
            if utils.data_point_exists(sample):
                print("Data point already exists, skipping")
                return

            utils.reset_log_file(sample)

            if not compile.compile_target(sample, sample.proj_dir): return

            #Make sure the original sample is not mucked up
            shutil.copyfile(sample.full_path, sample.working_sample)

            gen_inputs.log_coverage(sample)

            if (not gen_inputs.clean_evo_tests(sample)) or gen_inputs.check_for_empty_tests(sample): return

            if not gen_inputs.compile_evo_tests(sample, sample.newjar): return


        evotest = os.path.join(sample.workingdir, "evosuite-tests", sample.package_dir, sample.evo_file)
        if not args.only_reduce:
            '''
                Step 4: Execute evosuite tests
            '''

            my_test_oracle = executor.create_test_oracle(open(const.TEST_ORACLE).read(), sample)

            test_oracle_dst = os.path.join(sample.workingdir, "test.sh")
            with open(test_oracle_dst, "w") as f:
                f.write(my_test_oracle) 


            all_wraps = []

            count = 1

            #working_sample = os.path.join(sample.workingdir, os.path.basename(sample.full_path))
            working_sample_package = os.path.join(const.WORKING_DIR, sample.sample_num, sample.package_dir, sample.fname)
            print("running tests")
            sys.stdout.flush()
            test_execution_output = executor.run_tests(sample)
            if test_execution_output is None:
                print("Evosuite failure during execution of tests")
                utils.log(sample.log_file, "Evosuite execution", False)
                return

            traces = utils.extract_traces(test_execution_output)
            if traces is None: 
                print("Evosuite failure during execution of tests")
                utils.log(sample.log_file, "Evosuite execution", False)
                return

            '''
                Step 5: Wrapping loop (execute, wrap, compile, package, regen tests) LOOP
            '''

            while len(traces) > 0:
                if count > 10: 
                    print("ERROR MAX WHILE ITERATIONS REACHED")
                    utils.log(sample.log_file, "Wrapping", False)
                    sys.stdout.flush()
                    return
                
                wrapped_sample_content, wraps, err = wrapper.wrap(traces, sample)
                if err or wraps is None or wrapped_sample_content is None: return
                    
                all_wraps += [wraps]

                print("new wrapped content", wrapped_sample_content)
                print("writing wrapped content to", sample.working_sample)
                sys.stdout.flush()

                with open(sample.working_sample, "w") as f:
                    f.write(wrapped_sample_content)
                
                shutil.copy(sample.working_sample, working_sample_package)

                #compile_dir = os.path.join(const.WORKING_DIR, sample.sample_num, sample.package_dir)
                compile_dir = os.path.join(const.WORKING_DIR, sample.sample_num)

                if not compile.compile_target(sample, compile_dir, is_wrapped=True): return
                if not compile.package_target(sample, sample.workingdir): return

                if (not gen_inputs.run_evo(sample, sample.working_newjar)) or gen_inputs.check_for_empty_tests(sample): return

                #print("Evosuite test", evotest)
                #print(open(evotest).read())


                if (not gen_inputs.clean_evo_tests(sample)) or gen_inputs.check_for_empty_tests(sample): return

                if not gen_inputs.compile_evo_tests(sample, sample.working_newjar): return

                #print("Evosuite test after clean and compile", evotest)
                #print(open(evotest).read())

                sys.stdout.flush()
                count += 1

                test_execution_output = executor.run_tests(sample)
                if test_execution_output is None:
                    print("Evosuite failure during execution of tests")
                    utils.log(sample.log_file, "Evosuite execution", False)
                    return
                
                traces = utils.extract_traces(test_execution_output)
                
                if traces is None: 
                    print("Evosuite failure during execution of tests")
                    utils.log(sample.log_file, "Evosuite execution", False)
                    return

            wrap_log_file = sample.working_sample.replace("Sample", "WrapLog").replace(".java", ".json")
            
            with open(wrap_log_file, "w") as f:
                json.dump(all_wraps, f)

        if args.only_reduce:
            if not utils.data_point_exists(sample):
                print("Data point does not exist, skipping reduction")
                return
            else:
                print("Running just the reduction!")
                sys.stdout.flush()

        '''
            Step 5: Reduce
        '''


        #print("running persees")
        perses_success = reducer.run_perses(sample, evotest, sample.workingdir)

        utils.log(sample.log_file, "Perses Reduction", perses_success)
        #if os.path.exists("evosuite-tests"):
        #    shutil.rmtree("evosuite-tests")

        #sys.exit(1)
        if perses_success:
            print("Success!")
        sys.stdout.flush()

    except KeyboardInterrupt:
        sys.exit(1)
    except Exception as e:
        print("THREAD ERROR", e)
        traceback.print_exc()
        #print(e

def main():
    
    parallel = int(args.num_threads)
    #reset_flag = True if args.reset.lower() == "true" else False
    
    if parallel > 64:
        print("Too many threads... maxing out at 64")
        parallel = 64

    projects_to_process = utils.get_projects_to_process(args.project, args.project_file)
    samples_to_process = utils.get_samples_to_process(args.sample_file)

    if not os.path.exists(const.WORKING_DIR):
        os.mkdir(const.WORKING_DIR)

    log_dir = os.path.join(const.CWD, "logs")
    if not os.path.exists(log_dir):
        os.mkdir(log_dir)


    #print(projects_to_process)
    #sys.exit(1)
    #for project_to_process in projects_to_process:
    for proj in glob(const.BASE_DIR + "/*"):
        samples = []
        if not utils.should_process(projects_to_process, proj): continue
        
        #print(proj)
        #print(const.DATA_OUT.rstrip("/"))
        if not os.path.isdir(proj) or proj.startswith(const.DATA_OUT.rstrip("/")): continue

        for t, dirs, files in os.walk(proj):

            for f in files:
                if re.match(const.SAMPLE_RE, f): 
                    _id = re.search(const.SAMPLE_RE, f).group(1)
                    #print(_id[4:])
                    #if not (int(_id) == 100031): continue
                    _ids = [s[0] for s in samples_to_process]
                    if samples_to_process is not None and not int(_id) in _ids: continue
                    #print("Adding sample", os.path.join(t, f))

                    my_proj = [s[1] for s in samples_to_process if s[0] == int(_id)]
                    assert len(my_proj) == 1
                    my_proj = my_proj[0]
                    print(_id)

                    s = Sample(os.path.join(t, f))
                    #print(os.path.basename(proj), my_proj)
                    if not os.path.basename(proj) == my_proj: 
                        print("wrong folder")
                        continue                   
                    samples += [s]
                    #samples.append(os.path.join(t, f))

        print(f"Processing {len(samples)} for project {os.path.basename(proj)}")


        if args.print_stats:
            print("ONLY PRINTING STATS")
            utils.print_collection_stats(samples)
            exit(1)


        with Pool(parallel) as p:
            p.map(run_main, samples)

        #PRINT ALL OF THE STATS FOR THEAT PROJECT BEFORE MOVING ONTO THE NEXT
        utils.print_collection_stats(samples, len(samples))
        
        #exit(0)

if __name__ == '__main__':
    main()


