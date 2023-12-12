from glob import glob
import subprocess as sp
import javalang
import os
import json
import re

PREFIX = ""
LINUX_PREFIX = ""
PREFIX = LINUX_PREFIX

META_DIR = PREFIX + "projects/metadata/"
SRC_DIR  = PREFIX + "projects/"

OUT_DIR = PREFIX + "test"

generic_re = r"(public|protected|private)(\s*\S*)\s*\<\S*\>(\s\S*\s[^\(]*\([^\)]*\)\s*)"

def clean_sig(sig):
    #match = re.match(generic_re, sig)
    #if match:
    #    sig = match.group(2) + match.group(3) + ";"

    #remove visibility and trailing semicolon 
    #print(sig)
    first_space = sig.find(" ")
    first_open  = sig.find("(")
    if first_space < first_open:
        sig = " ".join(sig.split()[1:])[:-1]
    else:
        #remove semi
        sig = sig[:-1]


    idx = sig.find("(")+1

    #If there is a return type, remove it
    #a space before the first "(" indicates a return type?
    while " " in sig[:idx-1]:
        sig = " ".join(sig.split()[1:])
        #print("After removing return type", sig)
        idx = sig.find("(")+1
    
    #remove argument names
    end = sig.rfind(")")
    args = sig[idx:end].split()
    raw_arg_types = [arg for (idx, arg) in enumerate(args) if idx%2 == 0]   

    arg_types = []
    for arg_type in raw_arg_types:
        if "<" in arg_type:
            template_idx = arg_type.find("<")
            arg_types += [arg_type[:template_idx]]
        else:
            arg_types += [arg_type]

    if arg_types:
        sig = sig[:idx] + ", ".join(arg_types) + sig[end:]

    if "throws" in sig:
        #remove any throws 
        end = sig.rfind(")")
        sig = sig[:end+1]

    #print("After removing arg names", sig)

    return sig


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
            
        method_def += line + "\n"
    
    
    # method_sig = whitespace_re.sub(' ', method_sig).strip()
    # method_def = whitespace_re.sub(' ', method_def).strip()
    
    sig = clean_sig(method_sig)
    #print("og", method_sig)
    #print("sig", sig)
    return sig, method_def


for samplename in glob(PREFIX + "projects/data/*/Sample*_method.java"):
    proj_name = os.path.basename(os.path.dirname(samplename))
    sample_num = os.path.basename(samplename)

    out_file  = os.path.join(OUT_DIR, proj_name, sample_num).replace("Sample", "MUT").replace("_method", "")
    #context_file = out_file.replace("MUT", "context")
    if os.path.exists(out_file): continue #and os.path.exists(context_file): continue

    meta = json.load(open(os.path.join(META_DIR, proj_name, sample_num).replace(".java", ".json")))
    #print(meta["Class"])
    _class = meta["Class"].replace(".","/")
    _class = _class[0:_class.rfind("/")] + ".java"

    #.replace('/java', '.java')
    _method = meta["Method"]

    #print(samplename)
    class_file = os.path.join(SRC_DIR, proj_name, _class)
    if not os.path.exists(class_file):
        print(class_file, "doesn't exist!")
        continue
    class_content = open(class_file).read()
    class_lines = class_content.split("\n")

    try:
        class_ast = javalang.parse.parse(class_content)
    except javalang.parser.JavaSyntaxError:
        print("couldn't parse", class_file)
        continue

    MUT = None
    for path, node in list(class_ast.filter(javalang.tree.MethodDeclaration)) + list(class_ast.filter(javalang.tree.ConstructorDeclaration)):
        sig, MUT_txt = get_method_txt(class_lines, node.position.line-1)

        #doc = node.documentation if node.documentation else ""
    
        '''
        if sig[0:20] == _method[0:20]:
            print(sig, _method)
            print()
        '''
        #print(sig)
        if sig == _method.strip():
            MUT = MUT_txt


    if not MUT: 
        print("couldn't find a matching method!", _method)
        #break
        continue

        #print("writing to", out_file)


    _dir = os.path.join(OUT_DIR, proj_name)
    if not os.path.exists(_dir):
        os.mkdir(_dir)

    with open(out_file, "w") as f:
        f.write(MUT)

    #print("writing to", context_file)
    #with open(context_file, "w") as f:
    #    f.write(class_content)

    #break

