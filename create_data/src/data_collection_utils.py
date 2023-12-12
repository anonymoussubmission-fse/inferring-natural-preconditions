from enum import Enum
from tqdm import tqdm
from glob import glob
from collections import defaultdict
from Trace import Trace, Location
import subprocess as sp
import shutil
import os
import re
import json
import re
import sys
import random
from Sample import Sample
import data_collection_consts as const

mssing_symbol_regex = "symbol:\s*class (\S*)"
no_class_regex = "java\.lang\.NoClassDefFoundError: (\S*)"
ALL_PASSING_RE = "OK \([0-9]+ tests*\)"
NUM_TESTS_RE = "There (were|was) ([0-9]+) failures*:"
EACH_TEST_RE = "[0-9]*\) test([0-9]*)\S+"
LOCATION_RE = "at [^\(]*\.([^\(]*)\((\S+)\.java:([0-9]+)\)"
LIBRARY_RE = "at [^\(]*\.([^\(]*)\.([^\(]*)\(Unknown Source\)"
TRIM_RE = "\.\.\. [0-9]+ trimmed"

class SampleType(Enum):
    always_false = "FALSE",
    always_true = "TRUE",
    interesting = "INTERESTING"

class FailureMode(Enum):
    template_compilation = "TEMPLATE_COMPILATION",
    og_class_compilation = "CLASS_COMPILATION",
    packaging = "PACKAGING",
    evo_gen = "EVOSUITE_GENERATION",
    evo_clean = 3,
    evo_compile = 4,
    evo_exec = 5,
    evo_bad_tests = 6,
    wrap = 7,
    reduction = 8

class CompilationError(Enum):
    og_class_compilation_failure = 1,
    used_class_compilation_failure = 2,
    template_failure = 3,
    template_generic_failure = 4,
    template_final_variable = 5,
    class_structure = 6,
    wrap_failure = 7,
    OK = 8

def get_samples_to_process(sample_file):
    if not sample_file:
        return None
    
    samples = open(sample_file).read().strip().split("\n")
    sample_nums = []
    #get IDs
    for sample in samples:
        fname = os.path.basename(sample)
        proj = os.path.basename(os.path.dirname(sample))
        sample_num = re.search("Sample([0-9]+)_method_reduced\.java", fname).group(1)
        sample_nums += [(int(sample_num), proj)]
    
    return sample_nums


def should_process(projects_to_process, proj):
    for project_to_process in projects_to_process:
        if project_to_process in proj: return True
    
    return False


def get_projects_to_process(project, project_file):
    if project and project_file:
        print("ERROR. You can't specify a single project and a file to read from")
        sys.exit(1)
    elif not project and not project_file:
        #get all foldernames in BASE_DIR
        return [os.path.basename(os.path.dirname(f)) for f in glob(const.BASE_DIR + "/*") if os.path.isdir(f)]
    
    if project:
        return [project]
    
    if project_file:
        return [f for f in open(project_file).read().strip().split("\n") if f]
    

def spot_check(sample_type: SampleType=None, proj=None):
    sample_types = defaultdict(lambda: list())
    samples = [f for f in glob(const.DATA_OUT + "/*/*_method_reduced.java") if '35_corina'] #in f and not "broken" in f and not "elite" in f]

    if not sample_type:
        random.shuffle(samples)
        return samples[0:5]

    for f in samples:
        if proj and not proj in f: continue
        #trueif not '29_apbsmem' in f: continue
        content = open(f).read()

        true_match = re.search(const.TRUE_RE, content)
        false_match = re.search(const.FALSE_RE, content)

        if true_match and false_match:
            sample_types[SampleType.interesting] += [f]
        elif true_match:
            sample_types[SampleType.always_true] += [f]
        elif false_match:
            sample_types[SampleType.always_false] += [f]
        else:
            print("Data sample " + f + " is malformed")
            #raise Exception("Data sample " + sample.sample_num + " is malformed
    
    mine = sample_types[sample_type]
    random.shuffle(mine)
    return mine[0:5]


def log(log_file, stage, success, msg=None):
    if not msg: msg = ""
    with open(log_file, "a") as f:
        f.write(stage + " " + ("success" if success else "failure") + " " + msg)

def data_point_exists(sample):
    return os.path.exists(os.path.join(const.DATA_OUT, sample.proj_name, f"Sample{sample.sample_num}_method_reduced.java"))

def print_stats_standalone(project=None):
    samples = []
    for proj in tqdm(glob(const.BASE_DIR + "/*")):
        if project and not project in proj: continue

        if (not os.path.isdir(proj)) or  proj.startswith(const.DATA_OUT.rstrip("/")) or "metadata" in proj: continue

        for t, dirs, files in os.walk(proj):

            for f in files:
                if re.match(const.SAMPLE_RE, f): 
                    _id = re.search(const.SAMPLE_RE, f).group(1)

                    samples += [Sample(os.path.join(t, f))]


    broken_samples = [sample for sample in samples if not data_point_exists(sample)]
    success = [sample for sample in samples if data_point_exists(sample)]
    print()
    print("Successfully collected", len(success), "samples")

    print()
    print("==="*20)
    print("STATS FOR PROJECT", project if project else "ALL")
    print()



    print("{: <50} {: <10}".format("Started with", len(samples)))

    print_collection_stats(broken_samples, len(samples))

    print("Stats on the successfully collected samples")

    print_data_stats(success)

    print("==="*20)

def print_data_stats(samples):
    sample_types = defaultdict(lambda: defaultdict(lambda: 0))

    for sample in samples:
    #for sample in glob("/home/edinella/neural-testing/jd-cli-broken-numbering/data/29_apbsmem/*_method_reduced.java"):
        content = open(sample.data_sample_reduced).read()
        #content = "return false;"
        #content = open(sample).read()
        true_match = re.search(const.TRUE_RE, content)
        false_match = re.search(const.FALSE_RE, content)

        if true_match and false_match:
            sample_types[sample.proj_name][SampleType.interesting] += 1
        elif true_match:
            sample_types[sample.proj_name][SampleType.always_true] += 1
        elif false_match:
            sample_types[sample.proj_name][SampleType.always_false] += 1
        else:
            print("Data sample " + sample.sample_num + " is malformed")
            #raise Exception("Data sample " + sample.sample_num + " is malformed")
        
    projs = list(set([sample.proj_name for sample in samples]))
    total_int = 0 
    total_false = 0
    total_true = 0
    for proj in projs:
        print(proj)
        total = sum(sample_types[proj].values())
        print("{: <50} {: <10}".format("Total", total))
        k = SampleType.interesting
        v = sample_types[proj][k]
        total_int += v
        print("{: <50} {: <10}".format(str(k), v))

        k = SampleType.always_true
        v = sample_types[proj][k]
        total_true += v
        print("{: <50} {: <10}".format(str(k), v))

        k = SampleType.always_false
        v = sample_types[proj][k]
        total_false += v
        print("{: <50} {: <10}".format(str(k), v))

        #print("==="*20)

        k = SampleType.interesting
        v = sample_types[proj][k]
        #format v / total into a percentage with 2 decimal places
        p = "{:.2f}%".format(v / total * 100)
        print("{: <50} {: <10}".format(str(k), p))

        k = SampleType.always_true
        v = sample_types[proj][k]
        p = "{:.3f}%".format(v / total * 100)
        print("{: <50} {: <10}".format(str(k), p))

        k = SampleType.always_false
        v = sample_types[proj][k]
        p = "{:.2f}%".format(v / total * 100)
        print("{: <50} {: <10}".format(str(k), p))

    
        print("==="*20)

    total = 0

    for proj, types in sample_types.items():
        for t in types:
            total += sample_types[proj][t]
    #total = sum([ sample_types.values()])
    print("{: <50} {: <10}".format(str("total"), total))
    print("{: <50} {: <10}".format(str("total interesting"), total_int))
    print("{: <50} {: <10}".format(str("total true"), total_true))
    print("{: <50} {: <10}".format(str("total false"), total_false))

    #format v / total into a percentage with 2 decimal places
    p = "{:.2f}%".format(total_int / total * 100)
    print("{: <50} {: <10}".format("interesting", p))

    p = "{:.3f}%".format(total_true / total * 100)
    print("{: <50} {: <10}".format("true", p))

    p = "{:.2f}%".format(total_false / total * 100)
    print("{: <50} {: <10}".format("false", p))

    print("NUMBER OF PROJECTS", len(projs))

def print_collection_stats(samples, total):
    failure_types = defaultdict(lambda: 0)

    for sample in samples:
        if not os.path.exists(sample.log_file):
            in_progress = True #TODO: print this?
            continue
        failure_mode = analyze_failure_by_log(sample)
        if not failure_mode:
            failure_types["Success"] += 1
        else:
            #if failure_mode  == FailureMode.evo_exec:
            #    print(sample)
            failure_types[failure_mode] += 1
        
    k = FailureMode.template_compilation
    v = failure_types[k]
    total -= v
    print("{: <50} {: <10}".format("Left after " + str(k), total))

    k = FailureMode.og_class_compilation
    v = failure_types[k]
    total -= v
    print("{: <50} {: <10}".format("Left after " + str(k), total))

    k = FailureMode.packaging
    v = failure_types[k]
    total -= v
    print("{: <50} {: <10}".format("Left after " + str(k), total))


    k = FailureMode.evo_gen
    v = failure_types[k]
    total -= v
    print("{: <50} {: <10}".format("Left after " + str(k), total))


    k = FailureMode.evo_clean
    v = failure_types[k]
    total -= v
    print("{: <50} {: <10}".format("Left after " + str(k), total))


    k = FailureMode.evo_bad_tests
    v = failure_types[k]
    total -= v
    print("{: <50} {: <10}".format("Left after " + str(k), total))


    k = FailureMode.evo_compile
    v = failure_types[k]
    total -= v
    print("{: <50} {: <10}".format("Left after " + str(k), total))


    k = FailureMode.evo_exec
    v = failure_types[k]
    total -= v
    print("{: <50} {: <10}".format("Left after " + str(k), total))


    k = FailureMode.wrap
    v = failure_types[k]
    total -= v
    print("{: <50} {: <10}".format("Left after " + str(k), total))



    k = FailureMode.reduction
    v = failure_types[k]
    total -= v
    print("{: <50} {: <10}".format("Left after " + str(k), total))


    print("---"*20)
        
    #template compilation
    #class compilation
    #evosuite test generation
    #evo cleaning
    #evosuite test compilation
    #evosuite test execution
    #=========================
    #wrapping
    #reduction


def create_class_path(jars, exclude=None):
    cp = ""
    for jar in jars:
        if exclude and exclude in jar: continue
        cp += ":" + jar
    return cp




def get_evo_file(_id):
    evo_dir = f"/tmp/{_id}/evosuite-tests/"
    for root, dirs, files in os.walk(evo_dir):
        for f in files:
            if f.endswith("_method_ESTest.java"):
                return os.path.join(root, f)


def setup(sample):
    working_dir = os.path.join(const.WORKING_DIR, sample.sample_num)
    if os.path.exists(working_dir):
        shutil.rmtree(working_dir)
    os.mkdir(working_dir)

    reset_log_file(sample)

    test_oracle_dst = os.path.join(sample.workingdir, "test.sh")
    shutil.copy(const.TEST_ORACLE, test_oracle_dst)

def reset_log_file(sample):
    log_file = os.path.join(sample.workingdir, "log.txt")
    open(log_file, "w").close()

def modify_meta(metafile, cov):
    obj = json.load(open(metafile))
    obj["Coverage"] = cov
    #obj["Args"] = qualified_arg_type

    with open(metafile, "w") as f:
        json.dump(obj, f)

def get_meta(proj, _id):
    metafile = os.path.join(const.BASE_DIR, "metadata", proj, f"Sample{_id}_method.json")

    return json.load(open(metafile))

# @requires og_class to be the fully qualified class
def process_error_reason(og_class, classjars, err):

    og_class = og_class.replace(".", "-")
    print(og_class, classjars[0])

    if "error: unreachable statement" in err:
        return CompilationError.template_failure
    elif "cannot assign a value to final variable" in err:
        return CompilationError.template_final_variable

    elif "error: not an enclosing class" in err:
        return CompilationError.class_structure

    elif "is never thrown in body of corresponding try statement" in err:
        return CompilationError.template_failure

    elif "error: not a statement" in err:
        return CompilationError.template_failure

    elif any([os.path.basename(os.path.splitext(classjar)[0]) == og_class for classjar in classjars]) == 0:
                
        #    elif any([os.path.splitext(os.path.basename(classjar))[0] == og_class for classjar in classjars]) == 0:

        return CompilationError.og_class_compilation_failure

    elif "error: cannot find symbol" in err:
        m = re.search(mssing_symbol_regex, err)
        if not m:
            return CompilationError.template_failure
            #raise Exception("Error: cannot find symbol but no symbol found")
        
        missing_symbol = m.group(1)
        #print(missing_symbol)
        
        if len(missing_symbol) == 1 and missing_symbol.isupper():
            return CompilationError.template_generic_failure
        elif any([os.path.splitext(os.path.basename(classjar))[0] == missing_symbol for classjar in classjars]) == 0:
            return CompilationError.used_class_compilation_failure

    elif "java.lang.NoClassDefFoundError:" in err: 
        m = re.search(no_class_regex, err)
        if not m:
            raise Exception("Error: cannot find symbol but no symbol found")
    
        missing_symbol = m.group(1)

        if any([os.path.splitext(os.path.basename(classjar))[0] == missing_symbol for classjar in classjars]) == 0:
            return CompilationError.used_class_compilation_failure
        
            

    return CompilationError.template_failure
    #raise Exception("Could not analyze error reason", err)


def pretty_reduced_sample(reduced_sample):
    #cmd = f"java -jar {FORMAT_JAR} {reduced_sample} > {reduced_sample}"
    succ = True
    out = "/tmp/foo.txt"
    #cmd = f"java -jar {const.FORMAT_JAR} {reduced_sample} > {out}"
    cmd = f"{const.FORMAT_SCRIPT} --style=mozilla < {reduced_sample} > {out}" 
    try:
        sp.run(cmd, stdout=sys.stdout, stderr=sys.stderr, shell=True, check=True)
    except Exception as e:
        succ = False
        print(e)
        
    return out, succ


def get_jars(proj_name): 
    jars = glob(os.path.join(const.BASE_DIR, proj_name, "*.jar"))
    jar_files = [os.path.basename(j) for j in jars if (not j.endswith(".src.jar") and not "Sample" in j)]
    #print(proj_name)
    try:
        assert len(jar_files) == 1
    except:
        print("There should only be one jar in the top level project dir")
        print("Instead there are", len(jar_files))
        return None, None, None

    oldjar_full_path = os.path.join(const.BASE_DIR, proj_name, jar_files[0])

    class_jars = glob(os.path.join(const.BASE_DIR, proj_name, "class-jars", "*.jar"))
    #class_jars = [os.path.join(os.path.basename(os.path.dirname(p)), os.path.basename(p)) for p in class_jars]

    lib_jars = glob(os.path.join(const.BASE_DIR, proj_name, "libs", "*.jar"))
    #lib_jars = [os.path.join(os.path.basename(os.path.dirname(p)), os.path.basename(p)) for p in lib_jars]


    return oldjar_full_path, lib_jars, class_jars

def clean_exception_type(exception_type):
    if "evosuite" in exception_type: return "Exception"

    if ":" in exception_type:
       return exception_type[0:exception_type.find(":")]

    return exception_type

def skip_irrelevant_trace_lines(trace_lines):
    first_trace_line = 1
    found = False

    while first_trace_line < len(trace_lines):
        if all(["at " in t for t in trace_lines[first_trace_line:]]) \
                and (re.search(LOCATION_RE, trace_lines[first_trace_line]) or re.search(LIBRARY_RE, trace_lines[first_trace_line])):
            found = True
            break
        
        first_trace_line += 1

    if not found:
        print("Can't find the first trace line with at")
        return None
    
    return first_trace_line

def is_wellformed_trace(trace_lines):
    return all(["at " in t for t in trace_lines]) \
                and (re.search(LOCATION_RE, trace_lines[0] or re.search(LIBRARY_RE, trace_lines[0])))


def trace_contains_call_to_func(trace_lines):
    return any([".func(" in t for t in trace_lines])

# The first "relevant" line is either
# Case 1: The call to func is the last call made (no intra call)
# Case 2 and 3: There is a prefix of calls and func is lower in the stack trace
# The relevant line is either (1) the call to func or (2) the call right above func
def find_relevant_line(trace_lines): #trace_lines is a list of strings
    first_trace_line = 1

    if not is_wellformed_trace(trace_lines[first_trace_line:]):
        #REMOVE ALL IRRELEVANT PREFIX LINES... Sometimes the trace is too big and it says "trimmed"
        #In this case, the line after the exception has extra information that's not the trace
        first_trace_line = skip_irrelevant_trace_lines(trace_lines[first_trace_line:])
        if not first_trace_line: return None

    if not trace_contains_call_to_func(trace_lines[first_trace_line:]):
        return None
    
    func_line = None
    for idx, line in enumerate(trace_lines[first_trace_line:]):
        if ".func(" in line:
            func_line = idx

    if func_line == None:
        return None
    
    if func_line == 0:
        return first_trace_line
    else: 
        return first_trace_line + (func_line -1)

    return first_trace_line  

def get_loc_obj_from_line(lines, line_idx):
    '''
        Case 1: a line in func() causes the exception
        Case 2: a intraprocedural call (which we have code for..) causes the exception
        Case 3: library intra call causes exception (no line no)    
    '''
    line = lines[line_idx]

    m_loc = re.search(LOCATION_RE, line)
    m_unknown = re.search(LIBRARY_RE, line)

    if not m_loc and not m_unknown:
        return None
    
    if m_loc:
        method = m_loc.group(1)
        _class = m_loc.group(2) 
        lineno = m_loc.group(3)

    elif m_unknown:
        method = m_unknown.group(1)
        _class = m_unknown.group(2)

    #CALL TYPE
    if not method == "func":
        #We want the line number of the func line
        #func line should just be +1 of the 

        func_line = lines[line_idx + 1]

        #sanity check
        if not ".func(" in func_line:
            print("call line parsing error")
            return None
        
        m2 = re.search(LOCATION_RE, func_line)
        lineno = m2.group(3)

    if not lineno or not method or not _class: return None
    return Location(_class, method, lineno)


# Returns a list of Trace objects. If all tests pass, returns an empty list. If the test execution output is illformed, it will return None
# Example trace:
# There were 2 failures:
#1) test0(corina.print.Sample2157_method_ESTest)
#java.lang.NullPointerException
#	at corina.print.Sample2157_method.func(Sample2157_method.java:19)
#	at corina.print.Sample2157_method_ESTest.test0(Sample2157_method_ESTest.java:29)
#2) test1(corina.print.Sample2157_method_ESTest)
#java.lang.ClassCastException: javax.swing.DebugGraphics cannot be cast to java.awt.Graphics2D
#	at corina.print.Sample2157_method.func(Sample2157_method.java:17)
#	at corina.print.Sample2157_method_ESTest.test1(Sample2157_method_ESTest.java:43)
#
#FAILURES!!!
#Tests run: 3,  Failures: 2

def extract_traces(test_execution_output):

    #EXCEPTION_TYPE_RE = "(\S*\.[^:]+)"

    print(test_execution_output)

    m = re.search(ALL_PASSING_RE, test_execution_output)
    if m:
        return []

    m = re.search(NUM_TESTS_RE, test_execution_output)

    try:
        num_failed = m.group(2)
    except AttributeError as e:
        print("REGEX DIDNT MATCH")
        print(test_execution_output)
        raise e
        #exit(1)

    span = m.span()
    #print("num failed tests", num_failed)
    end = span[1]

    traces = test_execution_output[end:].strip()
    
    #splits it so its like [1) test0(corina.print.Sample2157_method_ESTest), java.lang.NullPointerException
    #	at corina.print.Sample2157_method.func(Sample2157_method.java:19)
    #	at corina.print.Sample2157_method_ESTest.test0(Sample2157_method_ESTest.java:29),
    #test2, trace2, ...]
    s = [elem.strip() for elem in re.split(EACH_TEST_RE, traces) if elem]

    if len(s) < 2:
        print("TEST REGEX DIDNT MATCH. ERROR EXECUTING THE TESTS!!")
        return None 

    traces = []
    evo = 0
    i = 0
    is_last_trace = False
    while i < len(s):
        if i+2 == len(s):
            is_last_trace = True

        test_num = s[i].strip()
        test_trace = s[i+1].strip()


        m = re.search(TRIM_RE, test_trace)
        if m:
            start = m.span()[0]
            test_trace = test_trace[0:start].strip()

        if is_last_trace:
            test_trace = test_trace.split("FAILURES!!!")[0].strip()
        
        trace_lines = test_trace.split("\n")

        exception_type = clean_exception_type(trace_lines[0])
        #if "evosuite" in exception_type:
        #    evo += 1
        #    i += 2
        #    continue

        #print(exception_type)
        #print("\n".join(trace_lines[1:]))

        first_trace_line = find_relevant_line(trace_lines)
        if not first_trace_line: return None        

        
        loc = get_loc_obj_from_line(trace_lines, first_trace_line)
        if not loc: return None

        traces += [Trace(test_num, exception_type, loc)]

        i += 2

    if not (len(traces) + evo) == int(num_failed): return None

    return traces

def analyze_failure_by_log(sample):
    #TODO: I should probably dump these in a more formal way, so I can comma seperate or something and only analyze the last one
    content = open(sample.log_file).read()

    _, _, classjars = sample.jars

    if "Wrapping failure" in content:
        return FailureMode.wrap
    elif "Compilation failure" in content:
        if not any([os.path.basename(os.path.splitext(classjar)[0]) == sample.og_class_jar for classjar in classjars]):
            #print(sample.sample_num)
            return FailureMode.og_class_compilation
        elif "Evosuite success" in content or "Evosuite Cleaning success" in content:
            return FailureMode.wrap
        else:
            return FailureMode.template_compilation
    elif "Packaging target method failure" in content:
        return FailureMode.packaging
    elif "Evosuite Compilation failure" in content:
        return FailureMode.evo_compile
    elif "Evosuite Cleaning failure" in content:
        return FailureMode.evo_clean
    elif "Evosuite failure" in content:
        return FailureMode.evo_gen
    elif "Perses Reduction failure" in content:
        return FailureMode.reduction
    elif "Evosuite execution failure" in content:
        return FailureMode.evo_exec
    elif "No Evosuite tests failure" in content:
        #print(sample.sample_num)
        return FailureMode.evo_bad_tests
    elif "Evosuite tests empty" in content:
        #print(sample.sample_num)
        return FailureMode.evo_bad_tests
    elif "failure" in content:
        print("UNKNOWN FAILURE TYPE")
        print(content)
        sys.exit(1)

    else:   
        try:
            assert "success" in content
        except AssertionError as e:
            #print(content)
            print(sample, "in progress?")
            #raise e

    return None

