import os
import sys

PREFIX=os.environ["PRECOND_HOME"]
PRECOND_LIBS=os.environ["PRECOND_LIBS"]
if not PREFIX:
    print("SET YOUR $PRECOND_HOME ENVIRONMENT VARIABLE")
    sys.exit(1)

if not PRECOND_LIBS:
    print("SET YOUR $PRECOND_LIBS ENVIRONMENT VARIABLE")
    sys.exit(1)


CWD = os.path.join(PREFIX, "precondition-data-collection", "create_data")

BASE_DIR=PREFIX + "projects/"
DATA_OUT=BASE_DIR + "data/"

PERSES_JAR= os.path.join(PRECOND_LIBS, "perses_deploy.jar")
WRAPPING_JAR=PREFIX + "precondition-data-collection/wrap-exceptions/target/wrap-exceptions-1.0-SNAPSHOT-shaded.jar"
#FORMAT_JAR=PREFIX + "google-java-format-1.13.0-all-deps.jar"
FORMAT_SCRIPT=PRECOND_LIBS+"astyle --style=mozilla"

WORKING_DIR=PREFIX + "working-dir"

SAMPLE_RE="Sample([0-9]+)_method\.java"
TRUE_RE = "return\s+true\s*;"
FALSE_RE = "return\s+false\s*;"

TEMPLATE_PREFIX=os.path.join(PREFIX, "precondition-data-collection", "create_data", "templates")
TEST_ORACLE=os.path.join(TEMPLATE_PREFIX,  "test_template.sh")
GEN_EVO_SCRIPT=os.path.join(TEMPLATE_PREFIX, "run_evosuite_template.sh")
CLEAN_EVO_SCRIPT=os.path.join(TEMPLATE_PREFIX, "clean_evosuite_template.sh")
COMPILE_EVO_SCRIPT=os.path.join(TEMPLATE_PREFIX, "compile_evosuite_template.sh")

