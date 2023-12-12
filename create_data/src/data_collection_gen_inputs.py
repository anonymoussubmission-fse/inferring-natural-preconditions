import data_collection_consts as const
import data_collection_utils as utils
import csv
import sys
import os
import shutil
import stat
import subprocess as sp

def check_for_empty_tests(sample):
    print("checking for empty tests")
    try:
        evocontent = open(os.path.join(sample.workingdir, "evosuite-tests", sample.package_dir, sample.fname.replace(".java", "_ESTest.java"))).read()
    except FileNotFoundError:
        print("couldn't find the evosuite generated tests")
        utils.log(sample.log_file, "No Evosuite tests", False)
        return True

    if not "public void test" in evocontent: 
        #print(os.path.join(sample.workingdir, "evosuite-tests", sample.package_dir, sample.fname.replace(".java", "_ESTest.java")))
        print("Evosuite tests are empty")
        utils.log(sample.log_file, "Evosuite tests empty", False)
        return True

    return False

def compile_evo_tests(sample, newjar):
    oldjar_full_path, libjars_full_path, classjars = sample.jars 

    libs = utils.create_class_path(libjars_full_path)
    classlibs = utils.create_class_path(classjars)

    print("COMPILING EVOSUITE TESTS")
    sys.stdout.flush()

    my_evo = os.path.join(sample.workingdir, "compile_evosuite_{}.sh".format(sample.sample_num))

    success = True

    cmd = "./compile_evosuite_{}.sh {} {} {} {} {}".format(sample.sample_num, newjar, oldjar_full_path, sample.package_dir, classlibs[1:], libs[1:])

    with open(os.path.join(sample.workingdir, "master_compile.sh"), "w") as f:
        f.write(cmd)

    shutil.copy(const.COMPILE_EVO_SCRIPT, my_evo)

    st = os.stat(my_evo)
    os.chmod(my_evo, st.st_mode | stat.S_IEXEC)

    sys.stdout.flush()

    try:
        result = sp.check_output(cmd, shell=True, stderr=sp.STDOUT, cwd=sample.workingdir).decode()
    except sp.CalledProcessError as e:
       success = False
       sys.stdout.flush()
    except Exception as e:
        print("THREAD ERROR")
        print(e)
        sys.stdout.flush()

    
    utils.log(sample.log_file, "Evosuite Compilation", success)

    return success 


def clean_evo_tests(sample):
    oldjar_full_path, libjars_full_path, classjars = sample.jars 

    libs = utils.create_class_path(libjars_full_path)
    classlibs = utils.create_class_path(classjars)

    print("CLEANING EVOSUITE")
    sys.stdout.flush()

    my_evo = os.path.join(sample.workingdir, "clean_evosuite_{}.sh".format(sample.sample_num))

    success = True

    cmd = "./clean_evosuite_{}.sh {} {}".format(sample.sample_num, sample.package_dir, sample.evo_file)

    with open(os.path.join(sample.workingdir, "master_clean.sh"), "w") as f:
        f.write(cmd)

    shutil.copy(const.CLEAN_EVO_SCRIPT, my_evo)

    st = os.stat(my_evo)
    os.chmod(my_evo, st.st_mode | stat.S_IEXEC)

    sys.stdout.flush()

    try:
        result = sp.check_output(cmd, shell=True, stderr=sp.STDOUT, cwd=sample.workingdir).decode()
        print(result)
    except sp.CalledProcessError as e:
       success = False
       print(e)
       sys.stdout.flush()
    except Exception as e:
        print("THREAD ERROR")
        print(e)
        sys.stdout.flush()
        success = False

    #print("hi")
    
    utils.log(sample.log_file, "Evosuite Cleaning", success)

    sys.stdout.flush()
    return success 

def run_evo(sample, newjar, shouldbreak=False):
    oldjar_full_path, libjars_full_path, classjars = sample.jars 

    libs = utils.create_class_path(libjars_full_path)
    classlibs = utils.create_class_path(classjars)

    print("RUNNING EVOSUITE")
    sys.stdout.flush()

    my_evo = os.path.join(sample.workingdir, "run_evosuite_{}.sh".format(sample.sample_num))

    #print(my_evo)
    #with open(my_evo, "w") as f:
    #     f.write(evo_script_content)

    evo_success = True
    msg = ""
    
    cmd = "./run_evosuite_{}.sh {} {} {} {} {}".format(sample.sample_num, sample.fully_qualified_class, newjar, oldjar_full_path, classlibs[1:], libs[1:])


    #print(cmd)
    master_run = os.path.join(sample.workingdir, "master_run.sh")
    with open(master_run, "w") as f:
        f.write(cmd)

    shutil.copy(const.GEN_EVO_SCRIPT, my_evo)

    st = os.stat(my_evo)
    os.chmod(my_evo, st.st_mode | stat.S_IEXEC)
    os.chmod(master_run, st.st_mode | stat.S_IEXEC)

    sys.stdout.flush()

    try:
        #result = sp.run(cmd, stdout=sp.PIPE, stderr=sp.STDOUT, shell=True, check=True, cwd=tmp_dir)
        #result = sp.check_output(cmd, shell=True, stderr=sp.STDOUT, cwd=sample.workingdir)
        result = sp.check_output("bash master_run.sh", shell=True, stderr=sp.STDOUT, cwd=sample.workingdir)
        sys.stdout.flush()

        result_str = result.decode()
        sys.stdout.flush()
        if "java.lang.ExceptionInInitializerError: null" in result_str and "java.lang.Class.forName0(Native Method)" in result_str:
            evo_success = False
            msg = "static block"

        elif "EvoSuite failed to generate any test case" in result_str:
            evo_success = False
        
        #if result and "No statistics has been saved because EvoSuite failed to generate any test case" in result.stderr.decode():
        #    evo_sucess = False
    except sp.CalledProcessError as e:
        if utils.get_evo_file(sample.sample_num):
            evo_success = True
        else:
            print("evosuite failed")
            evo_success = False
            sys.stdout.flush()

    except Exception as e:
        print("THREAD ERROR")
        print(e)
        sys.stdout.flush()
        #sys.exit(1)

    
    #os.remove(my_evo)
    print("done with evosuite")
    print("Evosuite success?", evo_success)
    print("MSG?", msg)
    evotest = os.path.join(sample.workingdir, "evosuite-tests", sample.package_dir, sample.evo_file)

    #print(open(evotest).read())

    sys.stdout.flush()
    utils.log(sample.log_file, "Evosuite", evo_success, msg)

    return evo_success 

def log_coverage(sample):
    stats_file = os.path.join(sample.workingdir, "evosuite-report/statistics.csv")

    if not os.path.exists(stats_file): return 

    with open(stats_file) as csvfile:
        csv_reader = csv.reader(csvfile)
        next(csv_reader)
        cov = next(csv_reader)[2]

    utils.modify_meta(sample.meta_file, cov)
    
