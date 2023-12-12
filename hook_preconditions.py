#from tree_hugger.core import JavaParser
import glob, javalang, os, json
from collections import defaultdict
import pandas as pd
import subprocess as sp


'''
    1) Loop over the methods in that we generated preconditions for (uh oh I might need the metadata) and store them 
    2) For each call in the randoop generated test suite, we check if there is a precondition label for it, if so, we have the potential to remove any FPs...
    3) That's only potential though. To get the actual stats we need to hook in the label and execute.. Idk exactly how to do this but it's definitely possible with a visitor or something
'''
samples = {}

DEBUG = True

if DEBUG:
    print("-"*35)
    print("DEBUG IS ON! NOT WRITING TO ANY FILES!")
    print("-"*35)

PREFIX = "/home/edinella/neural-testing/clean-projects/"
B_PREFIX = "/home/edinella/neural-testing/buggy-projects/"
DIRTY_PREFIX = "/home/edinella/neural-testing/fixed-projects/"

MODE = "RANDOOP" 

HOOKED = []
#HOOKED = [3,4,5,6,8,11,12,16,19,20,21,22,23,24]

#MODE = "EVOSUITE"

def get_label_from_method(proj_dir, method_key):
    proj_dir = proj_dir.replace("buggy", "fixed")
    if not proj_dir in samples.keys(): return None
    if not method_key in samples[proj_dir].keys():
        return None
    return samples[proj_dir][method_key]
    
def recurse(node_lst, lst):
    for child in node_lst:
        if not child: continue
        if isinstance(child, str) or isinstance(child, int): continue

        if isinstance(child, javalang.tree.MethodInvocation):
            lst.append(child)

        if isinstance(child, list) or isinstance(child, set):
            recurse(child, lst)
        else:
            recurse(child.children, lst)
    

def get_call(last_stmt):
    focal_calls = []
    recurse(last_stmt.children, focal_calls)

    if len(focal_calls) == 0:
        return None
    
    '''
    try:
        assert len(focal_calls) > 0
    except AssertionError:
        print("PROBLEM with get_call!")
        print(last_stmt)
        print(dir(last_stmt))
        sys.exit(1)
    '''

    return focal_calls[0]

def insert_oracle(f_evosuite):
    '''
        parse the evosuite test file
        For each test method, extract the test prefix
        Insert a call to the precondition
        Insert an oracle based on the results    
    '''

    content = open(f_evosuite).read()

    try:
        tree = javalang.parse.parse(content)
    except Exception as e:
        print("error parsing", f_randoop)
        raise e


def insert_check(f_randoop, class_methods):
    if "ErrorTest.java" in f_randoop: return
    content = open(f_randoop).read()

    try:
        tree = javalang.parse.parse(content)
    except Exception as e:
        print("error parsing", f_randoop)
        raise e

    class_dec = tree.types[0]
    methods = class_dec.methods
    content = content.split("\n")

    for method in methods:
        last_stmt = method.body[-1]
        last_call = get_call(last_stmt)
        if not last_call: continue

        if last_call.member == "assertTrue" or last_call.member == "assertFalse": continue
        #IF METHOD CALL IS IN A SUPERCLASS OR NOT IN THE ORIGINAL CLASS JUST CONTINUE!
        if not last_call.member in class_methods: continue


        line = last_call.position.line-1
        inputs = content[line][content[line].find(last_call.member)+ len(last_call.member):]
        og_target = last_call.qualifier
        og_call = content[line]
        replacement = f"boolean precondition_violated = {og_target}.{last_call.member}_precondition{inputs} if (precondition_violated) {{ return; }} else {{ {og_call} }}"

        content = content[0:line] +  [replacement] + content[line+1:]
        #print("REPLACING call to", last_call.member)

    if not DEBUG:
        with open(f_randoop, "w") as f:
            f.write("\n".join(content))

    print("WROTE TO", f_randoop)

def hook_all_methods(proj_dir, methods, og_class):
    for idx, method in enumerate(methods):
        if method.name.endswith("precondition"): continue

        args = params_to_str(method.parameters)

        rt_type = method.return_type.name if method.return_type else "void"
        method_key = " ".join(method.modifiers) + " " + rt_type + " " + method.name + "(" + args + ")"
        #print(proj_dir)
        label = get_label_from_method(proj_dir, method_key)
        
        #if DEBUG:
        #    print(method_key, label)
        #print(samples.keys())
        dummy_label = "public class Foo {\n public boolean func( <FOO> ) { return false; } }".replace("<FOO>", args)
        hook_label(og_class, method, idx, label, dummy_label)

def extract_label(raw_label, og_method_name):
    try:
        tree = javalang.parse.parse(raw_label)
    except Exception as e:
        print("error parsing", raw_label)
        raise e

    class_dec = tree.types[0]
    ms = class_dec.methods
    preconditions = [m for m in ms if m.name == "func"]
    if not len(preconditions) == 1:
        #raise Exception("No valid precondition in raw label:", raw_label)
        print("No valid precondition in raw label:", raw_label)
        return None
        
    method_sig, method_def = get_method_txt(raw_label.split("\n"), preconditions[0].position.line-1)
    method_def = method_def.replace("func(", f"{og_method_name}_precondition(")
    return method_def

def get_method_txt(lines, start_line):
    """
    lines: lines of file, assume each ends with \n
    start_line: first line of method decl
    """
    method_def = ''
    method_sig = ''
    depth = 0
    method_collected = False
    in_string = False

    for i in range(start_line, len(lines)):
        prev_char = ''
        line = lines[i]
        for col, char in enumerate(line):
            if char == "\""  and not prev_char == "\\": 
                in_string = not in_string
            elif char == '{' and not in_string:
                depth += 1
                #print('+', col, depth)

                if depth == 1: # open def, grab signature
                    method_sig = method_def + line[:col].strip() + ';'
            elif char == '}' and not in_string:
                depth -= 1
                #print('-', col, depth)
                if depth == 0: # end of method def
                    method_def += line[:col+1]
                    method_collected = True
                    #print("Method collected")
                    break
        
            prev_char = char
        if method_collected:
            break
            
        method_def += line + " "
    
    
    # method_sig = whitespace_re.sub(' ', method_sig).strip()
    # method_def = whitespace_re.sub(' ', method_def).strip()
    
    return method_sig, method_def


def insert_label(precondition, og_class, pos):
    content = open(og_class).read()
    if precondition in content: return

    #print("inserting", precondition, "into", og_class)

    lines = content.split("\n")
    lines = lines[0:pos] + [precondition] + lines[pos:]
    if not DEBUG:
        with open(og_class, "w") as f:
            f.write("\n".join(lines))

def get_pos(og_method, og_class):
    try:
        tree = javalang.parse.parse(open(og_class).read())
    except Exception as e:
        print("error parsing", _class)
        raise e

    class_dec = tree.types[0]
    ms = class_dec.methods
    return [m for m in ms if og_method == m.name][0].position.line-1

def method_in_class(m_name, _class):
    try:
        tree = javalang.parse.parse(open(_class).read())
    except Exception as e:
        print("error parsing", _class)
        raise e

    class_dec = tree.types[0]
    ms = class_dec.methods
    return bool([m for m in ms if m.name == m_name])


def recompile(project_dir):
    sp.call("defects4j compile", shell=True, cwd=project_dir)


def hook_label(og_class, og_method, method_idx, raw_label, dummy_label): 
    dummy_label = extract_label(dummy_label, og_method.name)
    if raw_label:
        label = extract_label(raw_label, og_method.name)

    if not raw_label or not label:
        label = dummy_label

    if "static" in og_method.modifiers:
        label = label.replace("public boolean", "public static boolean")

    pos = og_method.position.line-1+method_idx
    insert_label(label, og_class, pos)
    

def hook_precondition_checks(project_dir, proj_name, bug_num, class_methods):

    if MODE == "RANDOOP":
        test_prefix = RANDOOP_TESTS_DIR
    elif MODE == "EVOSUITE":
        test_prefix = EVOSUTIE_TESTS_DIR 
    else:
        sys.exit(1)

    test_dir = os.path.join(test_prefix, proj_name, "randoop", str(bug_num))
    
    test_dirs = set()
    for root, dirs, fs in os.walk(test_dir):
        for test in fs:
            if test.endswith(".java") and "ErrorTest" in test:
                test_dirs.add(os.path.join(root,test))
                if MODE == "RANDOOP":
                    insert_check(os.path.join(root,test), class_methods)
                else:
                    insert_oracle(os.path.join(root,test), class_methods)

    build_dir = get_build_dir(project_dir)
    compile_tests(os.path.join(project_dir, build_dir), test_dirs)
    

def get_src_dir(clean_proj_dir):
    return sp.check_output(f"defects4j export -p dir.src.classes", shell=True, cwd=clean_proj_dir).decode()


def get_build_dir(clean_proj_dir):
    return sp.check_output(f"defects4j export -p dir.bin.classes", shell=True, cwd=clean_proj_dir).decode()


def compile_tests(build_dir, test_dirs):
    libs = ["/home/edinella/neural-testing/junit-4.12.jar", "/home/edinella/neural-testing/defects4j/framework/lib/test_generation/generation/randoop-current.jar"]
    libs = ":".join(libs)

    cmd = f"javac -cp {libs}:{build_dir} *.java"

    for _dir in test_dirs:
        sp.call(cmd, cwd=os.path.dirname(_dir), shell=True)

def get_active_bugs(d4j_project_dir):
    active_bugs_df = pd.read_csv(d4j_project_dir + '/active-bugs.csv')
    return active_bugs_df['bug.id'].to_list()

def get_relevant_classes(project, bug_num):
    CLEAN_PREFIX = "/home/edinella/neural-testing/clean-projects/"
    proj_dir = f"{project}_{bug_num}_fixed"
    clean_dir = os.path.join(CLEAN_PREFIX, proj_dir)
   
    content = open(os.path.join(clean_dir, "defects4j.build.properties")).read()
    lines = content.split("\n")
    props = [line for line in lines if "d4j.classes.modified" in line]
    assert len(props) == 1 
    prop = props[0]
    _, modified_classes = prop.split("=")

    modified_classes = [modified_class.replace(".","/") + ".java" for modified_class in modified_classes.split(",") if modified_class]
    return modified_classes

def load_samples(f_samples):
    for sample in f_samples: #grab all the generated preconditions in d4j-data/Chart_4_fixed
        label = open(sample).read()

        proj_name = os.path.basename(os.path.dirname(sample))
        if not proj_name in samples.keys():
            samples[proj_name] = {}

        suffix = os.path.basename(sample).replace("_method_reduced.java", "")
        with open(os.path.join(META_DIR, suffix + ".json")) as f:
            method_key = json.load(f)["Method"]
            samples[proj_name][method_key] = label


def params_to_str(parameters):
    str_params = []
    for param in parameters:
        str_params += [param.type.name + " " + param.name]
        
    return ", ".join(str_params)

#jp = JavaParser("mylanguages.so")
#jp = JavaParser("/tmp/tree-sitter-repos/my-languages.so")
#jp = JavaParser("py_php_js_cpp_java_linux_64.so")
#ALL_PROJECTS = 'Chart Closure Collections Csv JacksonCore JacksonXml JxPath Math Time Cli Codec Compress Gson JacksonDatabind Jsoup Lang Mockito'.split() 
ALL_PROJECTS = ['Chart']

#DATA_DIR = "/home/edinella/neural-testing/reduction/d4j-data/Chart_6_fixed/"
DATA_DIR = "/home/edinella/neural-testing/reduction/d4j-data/*/"
META_DIR = "/home/edinella/neural-testing/reduction/d4j-metadata/"
D4J_PROJ_DIR  = "/home/edinella/neural-testing/defects4j/framework/projects/"
RANDOOP_TESTS_DIR = "/home/edinella/neural-testing/NL2Spec/experiments/d4j_bug_eval/randoop-generated-tests/"#Chart/randoop/4/
f_samples = glob.glob(DATA_DIR + "*_method_reduced.java")

load_samples(f_samples)

for project in ALL_PROJECTS:
    active_bugs = get_active_bugs(D4J_PROJ_DIR + '/' + project)

    for bug_num in active_bugs:
        #if not bug_num == 4: continue
        if bug_num == 1 or bug_num == 2: continue
        if bug_num in HOOKED: continue
        if not bug_num == 26: continue
        print("HOOKING LABELS FOR", bug_num)

        for version in ["buggy", "fixed"]:
            #if version == "fixed": continue
            
            class_methods = []
            for modified_class in get_relevant_classes(project, bug_num):
                proj_dir = f"{project}_{bug_num}_{version}"
                clean_dir = os.path.join(B_PREFIX if version == "buggy" else PREFIX, proj_dir)
                print(clean_dir, version)

                src_dir = get_src_dir(clean_dir)
                og_class = os.path.join(clean_dir, src_dir, modified_class)
                class_txt = open(og_class).read()

                try:
                    tree = javalang.parse.parse(class_txt)
                except Exception as e:
                    print("error parsing", modified_class)
                    raise e

                class_dec = tree.types[0]
                methods = class_dec.methods
                class_methods += [m.name for m in methods]

                hook_all_methods(proj_dir, methods, og_class)

                #project_dir = og_class[0:og_class.find("/", len(PREFIX))]
                #clean_dir = project_dir.replace("fixed-projects", "clean-projects" if version == "fixed" else "buggy-projects")
                recompile(clean_dir)
                #break

        hook_precondition_checks(clean_dir, project, bug_num, class_methods)
        #break
