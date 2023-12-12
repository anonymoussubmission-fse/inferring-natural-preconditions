import os
import shutil
import sys
import subprocess as sp
import data_collection_utils as utils
import data_collection_consts as const

     
def run_perses(sample, evotest, working_dir):
    print("RUNNING REDUCER")
    perses_success = True

    myoracle = os.path.join(working_dir, "test.sh")
    mysample = os.path.join(working_dir, sample.fname)
    metadata = sample.meta_file

    reduction_log = os.path.join(working_dir, sample.fname.replace("Sample", "ReductionLog").replace(".java", ".txt"))
    wrap_log = os.path.join(working_dir, sample.fname.replace("Sample", "WrapLog").replace(".java", ".json"))

    #if not os.path.exists(myoracle):
    #    shutil.copy(test_oracle, myoracle)
    
    if not os.path.exists(mysample):
        shutil.copy(sample.full_path, mysample)

    #copy the oracle and class into current directory
    cmd_perses = "java -jar {} --test-script {} --input-file {} --progress-dump-file {} --code-format COMPACT_ORIG_FORMAT".format(os.path.join(const.PERSES_JAR),  myoracle, mysample, reduction_log)

    #print(cmd_perses)

    with open(os.path.join(working_dir, "run_perses.sh"), "w") as f:
        f.write(cmd_perses)
    
    try:
        #sp.run(cmd_perses, stdout=sys.stdout, stderr=sys.stderr, shell=True, check=True)
        sp.run(cmd_perses, stdout=sp.DEVNULL, stderr=sp.DEVNULL, shell=True, check=True)
        reduced_sample = os.path.join(working_dir, "perses_node_priority_with_dfs_delta_reduced_" + sample.fname)
        data_dir = os.path.join(const.DATA_OUT, sample.proj_name)
        if not os.path.exists(data_dir):
            os.mkdir(data_dir)

        pretty_reduced, succ = utils.pretty_reduced_sample(reduced_sample)
        if not succ:
            return False

        shutil.copy(pretty_reduced, sample.data_sample_reduced)
        #shutil.copy(reduced_sample, data_dir)
        shutil.copy(evotest, data_dir)
        shutil.copy(reduction_log, data_dir)
        shutil.copy(wrap_log, data_dir)
        shutil.copy(sample.full_path, data_dir)

        shutil.copy(metadata, data_dir)
        #utils.modify_meta(os.path.join(data_dir, metaname), open(cov_file).read())

    except Exception as e:
        print(e)
        #sys.exit(1)
        perses_success = False

    #os.remove(os.path.join(BASE_DIR, class_name))
    return perses_success


