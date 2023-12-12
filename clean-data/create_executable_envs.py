import os
from glob import glob
import shutil

'''
    1) test.sh, evosuite-tests/tests.jar
    2) All project jars, classjars, and libjars
'''

PREFIX = "/home/edinella/neural-testing/"
DATA_DIR = PREFIX + "all-clean-data/"
BASE_DIR = PREFIX + "jd-cli/"
OUT_DIR = PREFIX + "excutable-environments/"
ENV_PREFIX="$ENV_PREFIX/"

KEY = "<?@#PRECONDITION?@#>"

ORACLE_CONTENTS = open(os.path.join(PREFIX, "jd-cli", "exec_env.sh")).read()

def create_test_oracle(precondition_wrapper_file, pred_file, oldjar, evojar, package_dir, evoclass, libjars, classjars, proj, samplenum):

    libs =  ":".join([lib for lib in libjars if not "junit" in lib])
    classlibs =  ":".join([lib.replace(OUT_DIR, ENV_PREFIX) for lib in classjars if not "junit" in lib])

    oldjar = oldjar.replace(BASE_DIR, ENV_PREFIX)
    evojar = evojar.replace(OUT_DIR, ENV_PREFIX)
    precondition_wrapper_file = precondition_wrapper_file.replace(DATA_DIR, "$DATADIR/")

    workdir = f"/tmp/eval_{samplenum}/"

    oracle_contents = ORACLE_CONTENTS.replace("<PRE_WRAPPER_FILE>", precondition_wrapper_file)
    oracle_contents = oracle_contents.replace("<WORKDIR>", workdir)
    oracle_contents = oracle_contents.replace("<PRED_PRE_FILE>", pred_file)
    oracle_contents = oracle_contents.replace("<OG_PROJ_JAR>", oldjar)
    oracle_contents = oracle_contents.replace("<TESTJAR>", evojar)
    oracle_contents = oracle_contents.replace("<PACKAGEDIR>", package_dir)
    oracle_contents = oracle_contents.replace("<EVOCLASS>", evoclass)
    oracle_contents = oracle_contents.replace("<LIBJARS>", libs)
    oracle_contents = oracle_contents.replace("<CLASSJARS>", classlibs)

    return oracle_contents


def get_jars(proj_name): 
    oldjar = None
    jar_files = [os.path.basename(j) for j in glob(os.path.join("../jd-cli/", proj_name, "*.jar"))]
    class_jars = [j for j in glob(os.path.join("../jd-cli/", proj_name, "class-jars", "*.jar"))]

    stripped_proj_name = proj_name[proj_name.find("_")+1:]
    full_jars = []

    for jar in jar_files:
        if jar.endswith(".src.jar") or "Sample" in jar: continue

        #print(proj_name, stripped_proj_name, jar)
        if stripped_proj_name in jar:
            oldjar = os.path.join(BASE_DIR, proj_name, jar)
        else:
            full_jars.append(os.path.join(BASE_DIR, proj_name, jar))

    return oldjar, full_jars, class_jars


#for sample in glob(os.path.join(DATA_DIR, '*', "Sample*_method.java")):
for sample in glob(os.path.join(DATA_DIR, '*', "Sample*_method.java")):
    #print(sample)
    #if not "83363" in sample: continue
    #print(sample)

    sample_num = int(os.path.basename(sample.replace("Sample", "").replace("_method.java", "")))
    proj = os.path.basename(os.path.dirname(sample)) 

    tmp_dir = "/tmp/" + str(sample_num)
    test_file = os.path.join(tmp_dir, "test.sh")
    if not os.path.exists(test_file): continue
    if not os.path.exists(os.path.join(BASE_DIR, proj)): continue

    out_dir = os.path.join(OUT_DIR, proj)
    if not os.path.exists(out_dir):
        os.mkdir(out_dir)


    out_test_file = os.path.join(out_dir, 'RunTests' + str(sample_num) + ".sh")


    proj_src_dir = os.path.dirname(sample).replace(DATA_DIR, BASE_DIR)
    oldjar, libjars, classjars = get_jars(proj_src_dir)
    if not oldjar: 
        print("no")
        continue

    print()
    print("---"*25)
    print(sample)

    if oldjar:
        dst_old_jar = oldjar.replace(BASE_DIR, OUT_DIR)
        if not os.path.exists(dst_old_jar):
            #print("copying", oldjar, "to", dst_old_jar)
            shutil.copy(oldjar, dst_old_jar)
    else:
        oldjar = ""


    dst_lib_jars = [c.replace(BASE_DIR, OUT_DIR) for c in libjars]

    if len(dst_lib_jars) and not os.path.exists(os.path.dirname(dst_lib_jars[0])):
        os.mkdir(os.path.dirname(dst_lib_jars[0]))

    for src, dst in zip(libjars, dst_lib_jars):
        #print("Copying", src, "to", dst)
        shutil.copy(src, dst)


    dst_class_jars = [c.replace(BASE_DIR, OUT_DIR) for c in classjars]
    if len(dst_class_jars) and not os.path.exists(os.path.dirname(dst_class_jars[0])):
        os.mkdir(os.path.dirname(dst_class_jars[0]))

    for src, dst in zip(classjars, dst_class_jars):
        #print("Copying", src, "to", dst)
        shutil.copy(src, dst)

    src_evojar = os.path.join(tmp_dir, "evosuite-tests", "tests.jar")
    evojar = os.path.join(OUT_DIR, proj, "Tests" + str(sample_num) + ".jar")

    #print("Copying", src_evojar, "to", evojar)
    shutil.copy(src_evojar, evojar)


    sample_content  = open(sample).read()
    p_start = sample_content.find("package") + len("package ")
    p_end = sample_content.find(";", p_start)
    package_name = sample_content[p_start:p_end]

    package_dir = package_name.replace(".","/")

    evoclasses = glob(os.path.join(tmp_dir, "evosuite-tests", package_dir, "*.class"))
    for evoclass in evoclasses:
        dst_evoclass = os.path.join(OUT_DIR, proj, os.path.basename(evoclass))
        #print("copying", evoclass, "to", dst_evoclass)
        shutil.copy(evoclass, dst_evoclass)

    evoclass = package_name + "." + "Sample" + str(sample_num) + "_method_ESTest"
    #print(evoclasses)
    
    pred_file = f"$PREDS/Label{sample_num}.java"
    out_test_content = create_test_oracle(sample, pred_file, oldjar, evojar, package_dir, evoclass, dst_lib_jars, dst_class_jars, proj, sample_num)

    #out_test_file = os.path.join(OUT_DIR, proj, "test" + str(sample_num) + ".sh")
    print("wrote to", out_test_file)
    with open(out_test_file, "w") as f:
        f.write(out_test_content)

