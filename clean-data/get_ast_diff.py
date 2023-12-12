import argparse
from collections import defaultdict
import json
import glob
import logging
import multiprocessing as mp
from ntpath import basename
import os
import time
import sys
import utils

'''
usage: get_ast_diff.py [-h] [--np NUM_PROCESSES]
                       input_folder output_folder

Generate asts and ast diffs from a corpus of buggy and fixed json pairs 

positional arguments:
  input_folder        folder containing buggy and fixed javascript pairs
  output_folder       folder to put generated ASTs and AST diffs

optional arguments:
  -h, --help          show this help message and exit
  
  --np NUM_PROCESSES  number of processing for multiprocessing
'''


logger = mp.log_to_stderr(logging.DEBUG)

DESC = 'Generate asts and ast diffs from a corpus of buggy and fixed javascript pairs'
parser = argparse.ArgumentParser(DESC)
parser.add_argument('--input_folder', default="", help='folder containing buggy and fixed javascript pairs')
parser.add_argument('--output_folder', default="", help='folder to put generated ASTs and AST diffs')

parser.add_argument("--np", dest="num_processes", type=int, help="# of processes", default=1)

def get_diff(s_name, p_name, bug_meta, failed_file):
    failed = 0

    f = {}
    f["sample_file"] = sample_name
    f["precondition_file"] = precondition_name

    ast_diff_fname = os.path.join(args.output_folder, utils.get_prefix(s_name) + "_ast_diff.txt")

    num_ast_diff = utils.get_ast_diff(s_name, p_name, ast_diff_fname, args.output_folder)

    #bug_meta["files_changed"][0]["ast_diff_file"] = ast_diff_fname

    #metadata = {}
    #metadata["file"] = basename(s_name) + ".java"
    #metadata["num_ast_diffs"] = num_ast_diff

    #bug_meta["files_changed"][0]["metadata"] = metadata


    if num_ast_diff < 0:
        utils.log_failure(failed_file, s_name, p_name) 
        failed = 1
        print("json diff failed")

    return bug_meta, failed

results = []

args = parser.parse_args()

if not args.output_folder or not args.input_folder: 
    parser.print_help()
    sys.exit(1)

if not os.path.isdir(args.output_folder): 
    os.mkdir(args.output_folder)

master_json = os.path.join(args.output_folder, "master_bug_metadata.json")

processed_file = os.path.join(args.output_folder, "processed.txt")


failed_samples_file = os.path.join(args.output_folder, "failed.txt")

if not os.path.exists(failed_samples_file):
    open(failed_samples_file, "x").close()

if not os.path.exists(processed_file):
    open(processed_file, "x").close()


processed_pairs = utils.get_already_processed(processed_file)

f_all = glob.glob(args.input_folder + "*.json")

#print(f_all)

is_processed_func = lambda p, e: e in p
pairs_to_process = utils.get_not_processed(f_all, processed_pairs, is_processed_func)

if not pairs_to_process:
    print("Already processed entire folder")
    sys.exit(1)

pairs = defaultdict(list)
#pool = mp.Pool(args.num_processes)

for f in pairs_to_process:
    sample_num = int(basename(f).replace("Sample", "").replace("Label","").replace(".json",""))
    pairs[sample_num].append(f)

count = 0
for prefix, v in pairs.items():
    print(prefix, v)
    assert len(v) == 2

    sample_name = v[0] if "Sample" in v[0] else v[1]
    precondition_name = v[1] if "Label" in v[1] else v[0]

    bug = {}
    bug["id"] = count

    result = get_diff(sample_name, precondition_name, bug, failed_samples_file)
    results.append(result)
    #pool.apply_async(standalone, args=(sample_name, precondition_name, bug, failed_samples_file), callback=results.append)

    count += 1

#pool.close()
#pool.join()

#func_to_write = lambda elem: elem["files_changed"][0]["metadata"]["file"]
#utils.cleanup(results, func_to_write, processed_pairs, is_processed_func, master_json, processed_file)


