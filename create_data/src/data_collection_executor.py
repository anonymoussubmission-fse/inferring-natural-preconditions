import data_collection_consts as const
import subprocess as sp
import sys
import os
import shutil


def create_test_oracle(template_oracle_contents, sample):

    oldjar_full_path, libjars_full_path, classjars = sample.jars 

    libs = ""
    for lib in libjars_full_path:
        if "junit" in lib: continue
        libs += ":" + lib


    classlibs = ""
    for jar in classjars:
        classlibs += ":" + os.path.join(const.BASE_DIR, sample.proj_name, jar)

    oracle_contents = template_oracle_contents.replace("<SRCFILE>", sample.fname).replace("<NEWJAR>", sample.newjar).replace("<OLDJAR>", oldjar_full_path).replace("<EVOJAR>", sample.evojar).replace("<PACKAGEDIR>", sample.package_dir).replace("<EVOCLASS>", sample.evo_class).replace("<LIBJARS>", libs[1:] if libs[1:] else "\"\"").replace("<CLASSJARS>", classlibs).replace("<TMPDIR>", sample.workingdir)

    return oracle_contents

def setup_test_execution(full_sample_path, class_name, tmp_dir):
    mysample = os.path.join(tmp_dir, class_name)

    #print("copying")
    #print(full_sample_path)
    #print("to")
    #print(mysample)
    if not os.path.exists(mysample):
        shutil.copy(full_sample_path, mysample)

def run_tests(sample):
    print("Executing EvoSuite tests for sample:", sample.sample_num)
    #print("running the tests in ", tmp_dir)
    #sys.stdout.flush()

    #myoracle = os.path.join(sample.workingdir, "test.sh")
    setup_test_execution(sample.full_path, sample.fname, sample.workingdir)
    
    #shutil.copy(test_oracle, myoracle)

    try:
        test_execution_output = sp.check_output("bash test.sh", cwd=sample.workingdir, shell=True, stderr=sp.PIPE).decode()
    except sp.CalledProcessError as e:
        test_execution_output = e.output.decode()
        #print("TRACE:")
        #print(test_execution_output)
        #return None

    #print(test_execution_output)
    if not "Time:" in test_execution_output:
        return None

    time_line = [idx for (idx, line) in enumerate(test_execution_output.split("\n")) if "Time:" in line]
    if not len(time_line) == 1:
        return None
    
    time_line = time_line[0]

    #time_idx = test_execution_output.find("Time:")
    #test_execution_output = test_execution_output[ime_idx+12:]

    test_execution_output = "\n".join(test_execution_output.split("\n")[time_line+1:])

    return test_execution_output
