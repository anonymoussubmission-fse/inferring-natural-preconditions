from collections import defaultdict
from glob import glob
import os
import sys
import subprocess as sp

PREFIX = "/home/edinella/neural-testing/NL2Spec/experiments/d4j_bug_eval/"


def run_eval(log_file, working_dir):
    #cmd = f"java -cp .:/home/edinella/neural-testing/junit-4.12.jar:/home/edinella/neural-testing/hamcrest-core-1.3.jar:{clean_dir}/build/ org.junit.runner.JUnitCore {pkg}"
    #print(_dir)
    cmd = f"bash eval.sh {_dir} {working_dir} {log_file}"
    print(cmd)
    with open(log_file, "w") as f:
        f.write("")
    try:
        #output = sp.check_output(cmd, cwd=_dir, shell=True).decode()
        f = open("/tmp/err.txt", "w")
        sp.call(cmd, shell=True, stdout=f, stderr=f)
        #sp.call(cmd, cwd=_dir, shell=True, stdout=f, stderr=f)
        #f.close()
    except sp.CalledProcessError as e:
        print(e.output)
        sys.exit(1)



triggers = defaultdict(lambda: 0)
project = "Chart"

err_keywords = ["initializationError", "sun.reflect.annotation.TypeNotPresentExceptionProxy"]
output_dir = os.path.join(os.getcwd(), "trigger-logs" )
if not os.path.exists(output_dir):
    os.mkdir(output_dir)

if not os.path.exists(os.path.join(output_dir, "bug_detection_log")):
    os.mkdir(os.path.join(output_dir, "bug_detection_log"))

if not os.path.exists(os.path.join(output_dir, "bug_detection_log", project)):
    os.mkdir(os.path.join(output_dir, "bug_detection_log", project))

if not os.path.exists(os.path.join(output_dir, "bug_detection_log", project, "randoop")):
    os.mkdir(os.path.join(output_dir, "bug_detection_log", project, "randoop"))


for _dir in glob(os.path.join(PREFIX, f"randoop-generated-tests/{project}/randoop/*")):
    bn = _dir.replace(PREFIX, "").replace(f"randoop-generated-tests/{project}/randoop/", "")
    if not bn == "16": continue
    print("EVALUATING", bn)
    #if not bn == "21": continue
    
    clean_dir = f"/home/edinella/neural-testing/clean-projects/{project}_{bn}_fixed"
    buggy_dir = f"/home/edinella/neural-testing/buggy-projects/{project}_{bn}_buggy"

    b_log_file = os.path.join(output_dir, "bug_detection_log", project, "randoop", f"{bn}b.{bn}.trigger.log")
    f_log_file = os.path.join(output_dir, "bug_detection_log", project, "randoop", f"{bn}f.{bn}.trigger.log")

    for root, dirs, fs in os.walk(_dir):
        for test in fs:
            if test.endswith(".java") and "ErrorTest" in test:
                pkg = root.replace(_dir, "")[1:].replace("/", ".") + ".ErrorTest"

                run_eval(b_log_file, buggy_dir)                            

                run_eval(f_log_file, clean_dir)                            

                break

