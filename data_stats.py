import os
import re
import json
from glob import glob
from collections import defaultdict


PROJ = "35_corina"

true_re = "return\s+true\s*;"
false_re = "return\s+false\s*;"
sample_re="Sample[0-9]+_method\.java"

MAC_PREFIX = ""
LINUX_PREFIX = ""
PREFIX = LINUX_PREFIX

DATA_DIR = PREFIX + "jd-cli/data"
SRC_DIR = PREFIX + "jd-cli/"

def get_evo_file(_id):
    evo_dir = f"/tmp/{_id}/evosuite-tests/"
    for root, dirs, files in os.walk(evo_dir):
        for f in files:
            if f.endswith("_method_ESTest.java"):
                return os.path.join(root, f)


def analyze_failure_by_log(_id, processed, failures, suc_by_class):
    log_file = f"/tmp/{_id}/log.txt"

    #open meta file 
    #print the original class
    metadata = json.load(open(f"/home/edinella/neural-testing/jd-cli/metadata/{PROJ}/Sample{_id}_method.json"))
    _class = metadata["Class"]
    package_dir = os.path.dirname(os.path.splitext(_class)[0].replace(".", "/"))
    #print(_class)
    #print()

    if not _id in processed:
        failures["not-processed"] += 1
        return 0
        
    content = open(log_file).read()
    if not content:
        failures["thread-error"] += 1
        return 0


    if "Compilation failure" in content:
        if "original class didn't compile" in content:
            failures["original-class-issue"]  += 1
            return 0
        elif "used class didn't compile" in content:
            failures["used-class-issue"]  += 1
            return 0

        elif "template failure" in content:
            failures["template-creation-failure"] += 1
            return 0

        elif "generic failure" in content:
            failures["template-generic"] += 1
            return 0

        elif "template final variable" in content:
            failures["template-final"] += 1
            return 0

        elif "Class structure issue" in content:
            failures["class-structure"] += 1
            return 0 

    elif "Tests failure" in content:
        if "Could not load class" in content:
            failures["class-load-issue"] += 1
            return 0

    elif "Evosuite failure" in content:
        #if not get_evo_file(_id):

        suc_by_class[_class]["failure"] += 1
        
        if "static block" in content:
            failures["evosuite-failure-static-block"] += 1
            return 0
        else:
            failures["evosuite-failure"] += 1
            print("failure", _id)
            return 0
        '''
        else: 
            failures["in-progress"] += 1
            return 0
        '''

    elif "Evosuite success" in content:
        
        evo_file = get_evo_file(_id)
        if not evo_file:
            print("NO EVO FILE FOR ID", _id, "BUT THE LOG SAYS SUCCESS!!")
            return 0
            #exit(1)

        c = open(get_evo_file(_id)).read()
        if not "public void test" in c:

            suc_by_class[_class]["failure"] += 1

            sample_content = open(os.path.join(SRC_DIR, PROJ, package_dir, f"Sample{_id}_method.java")).read()
            if "Thread " in sample_content:
                failures["multi-threading"] += 1
                #failures["evosuite-no-tests"] += 1
                return 0

            elif "pref" in sample_content:
                print("pref", _id)
                failures["prefs-file"] += 1
                return 0

            else:
                failures["evosuite-no-tests"] += 1
                print("no tests", _id)
                #print("Evo ran but 0 coverage", _id)
                #print(_class)
                #print("------------")
                
                return 0 
        suc_by_class[_class]["success"] += 1
        print("evo success", _id)
        return 1

    elif not "failure" in content:
        #print("in progress", _id)
        #print(content)

        failures["in-progress"] += 1
        return 0

    
        '''
        if "has private access in" in content:
            failures["compilation-failure"] += 1
        or "cannot assign a value to final variable" in content or "might not have been initialized" in content:
            failures["template-creation-failure"] += 1
        elif "error: cannot find symbol" in content:
            failures["var-extraction-failure"] += 1
        elif "error: invalid method declaration; return type required" in content:
            failures["constructor-bug"] += 1
        elif "WRAPPING FAILED No changes made" in content or "Unknown wrap type" in content:
            failures["wrapper-bug"] += 1
            if "Unknown wrap type" in content: print("AYYY", content)
        elif "call to super must be first statement in constructor" in content:
            failures["super-call"] += 1
        elif "error: call to this must be first statement in constructor" in content:
            failures["this-call"] += 1
        elif "error: missing return statement" in content:
            failures["empty-constructor"] += 1
        elif "class file has wrong version 55.0, should be 52.0" in content:
            failures["compiled-class-with-java11"] += 1
        return
    elif "Perses Reduction failure" in content:
        failures["perses-failure"] += 1
        return 
    elif "Evosuite tests broken failure" in content:
        failures["evosuite-failure"] += 1
        return
    '''

    print("UNKNOWN FAILURE TYPE", _id)
    print(content)
    print()
    print()

def get_project_data(proj_dir):
    _ids = []
    for t, dirs, files in os.walk(proj_dir):
        for f in files:
            m = re.match(sample_re, f)
            if not m: continue
            #count += 1
            _id = int(m[0].replace("Sample","").replace("_method.java",""))
            #print(_id)
            _ids.append(_id)
        
    return _ids



stats = defaultdict(lambda: 0)
total = 0

proj_stats = defaultdict(lambda: defaultdict(lambda: 0))

proj_data = {}
for proj_dir in glob(os.path.join(DATA_DIR, "*")):
    if not PROJ in proj_dir: continue
    proj_data[os.path.basename(proj_dir)] = get_project_data(proj_dir.replace(DATA_DIR, SRC_DIR))


sample_ids = []
sizes = defaultdict(lambda: 0)
for sample in glob(os.path.join(DATA_DIR, "*/*_reduced.java")):
    if not PROJ in sample: continue
    suffix = sample.replace(DATA_DIR, "")
    proj = suffix[1:][0:suffix[1:].find("/")]
    #print(suffix)


    _id = int(os.path.basename(sample).replace("Sample","").replace("_method_reduced.java",""))
    sample_ids.append(_id)

    content = open(sample).read()
    size = len(content[content.find("func"):].split("\n"))
    #if size > 10: print(content)
    sizes[size] += 1
    true_match = re.search(true_re, content)
    false_match = re.search(false_re, content)
    if true_match and false_match:
        #print(sample)
        stats["interesting"] += 1
        proj_stats[proj]["interesting"] += 1

    elif true_match:
        stats["always-true"] += 1
        proj_stats[proj]["always-true"] += 1

        #print(sample)
    elif false_match:
        stats["always-false"] += 1
        proj_stats[proj]["always-false"] += 1

    #else: 
    #    #print(sample)
    #stats["none"] += 1
    #proj_stats[proj]["none"] += 1

            

    total += 1
        
total_possible = 0
for k, v in proj_stats.items():
    print("-"*100)
    print(k)
    print("-"*100)

    proj_total = v["always-true"] + v["always-false"] + v["interesting"]
    print("Always true:", v["always-true"])
    print("Always false:", v["always-false"])
    print("Interesting:", v["interesting"])
    #print("Broken:", v["none"])
    print(f"Total: {proj_total}/{len(proj_data[k])}")
    total_possible += len(proj_data[k])

failures = {"not-processed": 0, "original-class-issue": 0, "used-class-issue": 0, "compilation-failure": 0, "evosuite-failure": 0, "evosuite-failure-static-block": 0, "evosuite-no-tests": 0, "var-extraction-failure": 0, "template-creation-failure": 0, "constructor-bug": 0, "wrapper-bug":0, "super-call": 0, "this-call": 0, "empty-constructor": 0, "perses-failure": 0, "compiled-class-with-java11": 0, "in-progress": 0, "class-load-issue": 0, "template-generic": 0, "template-final": 0, "class-structure": 0, "thread-error": 0, "multi-threading": 0, "prefs-file": 0}
processed = [int(s) for s in open("processed.txt").read().split("\n") if s]

suc_by_class = defaultdict(lambda: {"success": 0, "failure": 0})

evo_success = 0
for k, v in proj_data.items():
    for _id in v:
        #print(_id)
        if not _id in sample_ids:
            evo_success += analyze_failure_by_log(_id, processed, failures, suc_by_class)
            


print("-"*100)
print("TOTAL STATS")
print("-"*100)
print("Always true:", stats["always-true"])
print("Always false:", stats["always-false"])
print("Interesting:", stats["interesting"])
#print("Broken:", stats["none"])
print("-"*100)
print("Total:", total - stats["none"], "out of", total_possible, "possible")
    
print("="*100)
print("FAILURE RATES")
print("Evo success", evo_success)
print("="*100)
print("IN PROGRESS", failures["in-progress"])
print("Not yet processed", failures["not-processed"])
print("-"*100)
print("COMPILATION ERRORS")
print("-"*100)
print("Original class didn't compile", failures["original-class-issue"])
print("Used class didn't compile", failures["used-class-issue"])
print("Class load issue", failures["class-load-issue"])
#print("Compilation failure", failures["compilation-failure"])
#print("Variable extraction failure", failures["var-extraction-failure"])
print("Template Creation failure", failures["template-creation-failure"])
print("Template Generics", failures["template-generic"])
#print("Constructor bug", failures["constructor-bug"])
print("Template Final Var", failures["template-final"])
print("Class structure", failures["class-structure"])
total_comp_failures  = 0
for k in ["original-class-issue", "used-class-issue", "class-load-issue", "template-creation-failure", "template-generic", "template-final", "class-structure"]:
    total_comp_failures += failures[k]

print("TOTAL COMPILATION FAILURES", total_comp_failures)
print("-"*100)
print("EVOSUITE FAILURES")
print("-"*100)
print("Didn't even run", failures["evosuite-failure"])
print("Original class has a static block", failures["evosuite-failure-static-block"])
print("Func has multi threading", failures["multi-threading"])
print("bad mock of prefs file", failures["prefs-file"])
print("no tests", failures["evosuite-no-tests"]) 
print("TOTAL EVOSUITE FAILURES", failures["evosuite-no-tests"] + failures["prefs-file"] + failures["multi-threading"] + failures["evosuite-failure-static-block"] + failures["evosuite-failure"])
print("-"*100)
print("Thread error", failures["thread-error"])
#print("Super/this call", failures["super-call"] + failures["this-call"])
#print("Wrapper bug", failures["wrapper-bug"])
#print("Empty Constructor", failures["empty-constructor"])
#print("Perses Failure", failures["perses-failure"])
#print("Compiled with java 11", failures["compiled-class-with-java11"])

'''
for k, v in suc_by_class.items():
    print(k)
    print("\t"+str(v))
    print()
'''


total_possible = len(get_project_data(os.path.join(SRC_DIR, PROJ)))

print(sum(failures.values()) + evo_success, total_possible)
assert sum(failures.values()) + evo_success == total_possible


'''
print("-"*100)
for s in sorted(sizes):
    print(s, sizes[s])
'''
