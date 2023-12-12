from ntpath import basename
import os
import json
import subprocess

JSON_DIFF_SCRIPT = "run_json_diff.js"

def log_failure(failed_file, b_fname, f_fname):
    with open(failed_file, "a") as f:
        f.write(b_fname + " " + f_fname + "\n")

def cleanup(results, func_to_write, processed, is_processed_func, master_file, processed_file):
    num_failed = sum([entry[1] for entry in results])
    ast_results = [entry[0] for entry in results if entry[0]]

    print(len(ast_results), "processed,", num_failed, "failed")

    write_to_processed(ast_results, func_to_write, processed_file)

    for bug in ast_results:
        assert bug["files_changed"] and not is_processed_func(processed, bug)

    if os.path.exists(master_file):
        with open(master_file) as f:
            json_array = json.load(f)
    else:
        json_array = []

    json_array = json_array + ast_results
    json_array = sorted(json_array, key=lambda bug: int(bug["id"]))

    with open(master_file, 'w') as outfile:
        print("wrote to master_bug", master_file)
        #print("adding: ", json_array)
        json.dump(json_array, outfile, indent=4, sort_keys=True)


def write_to_processed(processed, func_to_write, processed_file):
    to_write = ""

    for elem in processed:
        to_write += func_to_write(elem) + "\n"

    if os.path.exists(processed_file):
        with open(processed_file, "a") as f:
            f.write(to_write)

    else:
        with open(processed_file, "w") as f:
            f.write(to_write)

def get_prefix(filename):
    tmp = basename(filename)

    suffixes = ["_buggy.js", "_buggy_babel.js", "_fixed.js", "_fixed_babel.js"]

    itr = 0
    end = -1
    while end < 0 and itr < len(suffixes):
        end = tmp.rfind(suffixes[itr])
        itr += 1

    return tmp[0:end]

def get_ast_diff(s_fname, p_fname, ast_name, out_path):

    try:
        args = ["node", JSON_DIFF_SCRIPT, s_fname, p_fname, ast_name]
        subprocess.call(args)

    except Exception as e:
        #print(e)
        #print("json diff failed")
        return -1

    with open(ast_name, "r") as f:
        try:
            obj = json.load(f)
        except RecursionError as e:
            print("JSON too big")
            return -1

    s_ast = json.load(open(s_fname))

    clean_ast = []

    idx = 0
    while idx < len(obj):
        item = obj[idx]
        new_node = is_add_try(idx, obj, s_ast)
        if new_node:
            #REPLACE obj[idx] -> obj[idx+3] with new_node
            clean_ast.append(new_node)
            idx += 4
            continue

        new_node = is_null_check(idx, obj)
        if new_node:
            print("null check!")
            clean_ast.append(new_node)
            idx += 4
            continue

        '''
        new_node = aob_check(idx, obj)
        if new_node:
            ????
        '''
        
        clean_ast.append(item)
        idx += 1


    with open(ast_name, "w") as f:
        try:
            json.dump(clean_ast, f, indent=4)
        except RecursionError as e:
            print("JSON too big")
            return -1

    return len(clean_ast)


def find_match(try_expr, d):
    if d == try_expr:
        return d

    if isinstance(d, dict):
        #print(d.keys())
        for k in d.keys():
            if isinstance(d[k], list):
                for i in d[k]:
                    m = find_match(try_expr, i)
                    if m: return m
            else:
                m = find_match(try_expr, d[k])
                if m: return m

def is_null_check(idx, obj):
    node = obj[idx]
    if not "value" in node: return None

    _if = node["value"]
    check1 = _if["!"].endswith("IfStmt")
    if not check1: return None

    check2 = _if["condition"]["!"].endswith("BinaryExpr") 
    if not check2: return None

    check3 = _if["condition"]["right"]["!"].endswith("NullLiteralExpr")

    #.endswith("NullLiteralExpr") and binOpNode["operator"] == "EQUALS"
    if not check3: return None

    thenNode = _if["thenStmt"]
    check4 = thenNode["!"].endswith("ReturnStmt") and thenNode["expression"]["!"].endswith("BooleanLiteralExpr") and thenNode["expression"]["value"] == "true"
    if not check4: return None

    body = _if["condition"]["left"] 

    new_node = {}
    new_node["op"] = "add-npe-check"
    new_node["path"] = node["path"]
    new_node["value"] = body

    return new_node

def is_add_try(idx, obj, s_ast):
    node = obj[idx]

    if idx + 1 >= len(obj): return None
    nextNode = obj[idx+1]

    if idx + 3 >= len(obj): return None
    tryNode = obj[idx+3]

    if not 'value' in node: return None
    firstCheck =  nextNode["op"] == "add" and node["value"] == "com.github.javaparser.ast.stmt.TryStmt" and  nextNode["path"].endswith("catchClauses") 
    if not firstCheck: return None

    #print(nextNode["value"][0]["body"]["statements"][0])
    secondCheck = nextNode["value"][0]["!"] == "com.github.javaparser.ast.stmt.CatchClause" and nextNode["value"][0]["parameter"]["name"]["identifier"] == "e" and nextNode["value"][0]["body"]["statements"][0]["!"] == "com.github.javaparser.ast.stmt.ReturnStmt" and nextNode["value"][0]["body"]["statements"][0]["expression"]["value"] == "true"
    if not secondCheck: return None

    thirdCheck = tryNode["op"] == "add" and tryNode["path"].endswith("tryBlock")
    if not thirdCheck: return None

    expressionStmt = find_match(tryNode["value"]["statements"][0], s_ast)
    if not expressionStmt:
        print("NOO MATCH!")
        sys.exit(1)

    new_node = {}
    new_node["op"] = "add-try"
    path = tryNode["path"]
    new_node["value"] = expressionStmt["path"]

    return new_node

def get_not_processed(whole, processed, is_processed_func):
    return [elem for elem in whole if not is_processed_func(processed, elem)]

def get_already_processed(processed_file):
    with(open(processed_file, "r")) as f:
        processed_pairs = f.read()
        processed_pairs = processed_pairs.splitlines()

    return processed_pairs


