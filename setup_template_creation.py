import subprocess as sp
import os
from glob import glob
import sys

PRECOND_HOME=os.environ["PRECOND_HOME"]
PRECOND_LIBS=os.environ["PRECOND_LIBS"]
if not PRECOND_HOME:
    print("SET YOUR $PRECOND_HOME ENVIRONMENT VARIABLE")
    sys.exit(1)

if not PRECOND_LIBS:
    print("SET YOUR $PRECOND_LIBS ENVIRONMENT VARIABLE")
    sys.exit(1)

TEMPLATE_DIR=os.path.join(PRECOND_HOME, "precondition-data-collection/template-creation/")
PREFIX=os.path.join(PRECOND_HOME, "projects")



def is_SF100(proj):
    all_projs = glob(os.path.join(PRECOND_LIBS, "SF100/*"))
    return proj in [os.path.basename(p) for p in all_projs]


projs = open(os.path.join(PRECOND_LIBS, "projects.txt")).read().strip().split("\n")


if not len(sys.argv) > 1:
    print("YOU NEED TO SET THE NEXT SAMPLE NUM TO START WITH FOR THIS SERVER")
    exit(1)

last_sample_num = int(sys.argv[1])
for proj in projs:
        

    print("=="*50)
    print("PROCESSING", proj)
    print()
    
    cmd = "bash setup_before_template_creation.sh " + proj + " " + str(last_sample_num)

    if is_SF100(proj):
        print("This is an SF100 project")
        cmd += " True"

    sp.call(cmd, shell=True)
    #prev_proj = proj

    cmd = "bash last_sample_num.sh " + proj
    output = sp.check_output(cmd, shell=True).decode()
    print(output)
    num = int(output.strip().split("\n")[-1])

    if num < last_sample_num:
        assert (num == 1)
        last_sample_num += 1
    else:
        last_sample_num = num

    print("NEXT SAMPLE", last_sample_num)
