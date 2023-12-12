from glob import glob
from tree_hugger.core import JavaParser
import subprocess as sp
import pandas as pd
import os

'''
Print the following stats
1) Number of relevant classes (C)
2) Number of samples that should be created (C * M(C)) where M is number of methods in C
3) Number of precondition data samples successfully collected
'''

LINUX_PREFIX = ""
MAC_PREFIX = ""

#PREFIX = LINUX_PREFIX
PREFIX = MAC_PREFIX

STAT_JAR = PREFIX + "reduction/stats/target/Method-Stats-1.0-SNAPSHOT-shaded.jar"

DEFECTS4J = False

def get_project_layout(d4j_project_dir):
    project_layout_df = pd.read_csv(d4j_project_dir + '/dir-layout.csv',index_col=0, 
                                    names=['src_dir', 'test_dir'])
    return project_layout_df.to_dict()

def get_active_bugs(d4j_project_dir):
    active_bugs_df = pd.read_csv(d4j_project_dir + '/active-bugs.csv', index_col=0)
    bug_scm_hashes = active_bugs_df[['revision.id.buggy', 'revision.id.fixed']].to_dict()
    return active_bugs_df.index.to_list(), bug_scm_hashes



def get_class_dec(class_file):
    try:
        with open(class_file) as f:
            class_txt = f.read()
            
        with open(class_file) as f:
            class_lines = f.readlines()
    
    except Exception as e:
        print('ERROR READING:', class_file)
        raise e

    try:
        #tree = javalang.parse.parse(class_txt)
        jp.parse_file(class_file)
        #tree = jp.parse(class_txt)
    except Exception as e:
        print("error parsing", class_file)
        raise e

    class_dec = None
    #class_dec = tree.types[0]
    return class_dec, class_lines


def get_classes(project):
    if DEFECTS4J:
        bug_num, classes = bug.split(",") 
        return classes.replace('"',"").split(";")
    else:
        classes = []
        for root, dirs, fs in os.walk(project):
            for _f in fs:
                if _f.endswith(".java") and not "Sample" in _f:
                    classes.append(os.path.join(root, _f))

        return classes

jp = JavaParser("build/my-languages.so")

if DEFECTS4J:
    #ALL_PROJECTS = 'Chart Closure Collections Csv JacksonCore JacksonXml JxPath Math Time Cli Codec Compress Gson JacksonDatabind Jsoup Lang Mockito'.split() 
    ALL_PROJECTS = ['Chart']

    BUILD_PROPERTY_FILE = "defects4j.build.properties"
    D4J_DIR = ""

else:
    ALL_PROJECTS = glob(PREFIX + "/jd-cli/*")

C, M = 0, 0

for project in ALL_PROJECTS:
    #if not "102_instagram-java" in project: continue
    if DEFECTS4J:
        out = sp.check_output("defects4j query -p {} -q \"classes.modified\"".format(project), shell=True).decode()

        query_out = out.strip().split("\n")
        d4j_project_dir = os.path.join(D4J_DIR, project)
        project_layout = get_project_layout(d4j_project_dir)
        _, bug_scm_hashes = get_active_bugs(d4j_project_dir)
        
    #else:
    #    query_out = 

    #for bug in query_out:
    #    if not bug: continue
    

    all_classes = get_classes(project)
    #print(all_classes)
    C += len(all_classes)
    proj_m = 0
    #print(project, len(all_classes))
    for _class in all_classes:
        if DEFECTS4J:
            try:
                bug_hash = bug_scm_hashes['revision.id.buggy'][int(bug_num)]
                src_dir = project_layout['src_dir'][bug_hash]
            except:
                print('ERROR: no bug hash/dir for', bug_num, project)
                #continue

            suffix = _class.replace(".","/") + ".java"
            classpath = os.path.join("../fixed-projects", f"{project}_{bug_num}_fixed", src_dir, suffix)

        else:
            classpath = _class

        cmd = f"java -jar {STAT_JAR} {classpath}"
        try:
            num_methods = int(sp.check_output(cmd, shell=True).decode())
        except Exception as e:
            print(e)
            continue

        proj_m += num_methods
        M += num_methods


    print(project, proj_m)
print(C, M)



