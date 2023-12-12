from collections import defaultdict
from utils import tuple_iter
import subprocess as sp
import os
import math
import random
import csv
import json


DATA_DIR = "/home/edinella/neural-testing/jd-cli/data/"
OFFSETS = { "NPE": 1,
            "call": 5, 
            "AOB": 1,
            "negArray": 1
           }

def flatten(t):
    return [item for sublist in t for item in sublist]

def normalize(content):
    return " ".join(content.split())

def get_offset(cur, offsets):
    cur_offset = 0
    for line, offset in offsets.items(): #anything after line needs to be adjusted by offset
        if cur["lineNo"] > line:
            cur_offset += offset

    return cur_offset

def adjustLinenos(actions):
    offsets = defaultdict(lambda: 0)
    new_actions = []
    print("ACTIONS")
    for i, actionset in enumerate(actions):
        new_actionset = []
        if i == 0:
            for action in actionset:
                print(action)
                offsets[action["lineNo"]] += OFFSETS[action["type"]]

                new_actionset += [action]

            new_actions += [new_actionset]
            continue


        for action in actionset:
            cur_offset = get_offset(action, offsets)
            print(action, cur_offset)
            #update the offsets based on these for the next actionset!
            new_actionset += [{"type": action["type"], "lineNo": action["lineNo"] - cur_offset, "exceptionType": action["exceptionType"]}]
            offsets[action["lineNo"]-cur_offset] += OFFSETS[action["type"]]
            print("OFFSETS", offsets)

        new_actions += [new_actionset]

    print()
    print("NEW ACTIONS")
    for actionset in new_actions:
        for action in actionset:
            print(action)
    sys.exit(1)
        #otherwise.... check the numbers. If the line numbers come after any of the edits in the previous steps, they need to be adjusted. 
    print(actionset)

    sys.exit(1)
data = []
skipped, total, avg = 0, 0, 0
for (src, pre, reduction_log, action_log) in tuple_iter(DATA_DIR):
    actions = json.load(open(action_log))
    src_content = open(src).read()

    if "184671" in src_content: continue

    print(src)


    #FIX LINENOS!
    if not len(actions) == 1:
        #print(actions)
        skipped += 1
        continue
        adjustLinenos(actions)
        actions = flatten(actions)

        
    else:
        actions = actions[0]

    if any([action for action in actions if not (action["type"] == "NPE")]): # or action["type"] == "call")]):
        skipped += 1
        continue

    try:
        samples_str = sp.check_output(f"java -jar enumerate-actions/target/enumerate-actions-1.0-SNAPSHOT-shaded.jar {src}", shell=True).decode()
    except Exception as e:
        #print(e.output)
        skipped+=1
        continue

    samples = json.loads(samples_str)

    for sample in samples:
        dummy_action = {"type": "NPE", "lineNo": sample["lineno"], "exceptionType": "NullPointerException"}

        if dummy_action in actions:
            label = 1
        else:
            label = 0

        clean_src_content = open("clean-data/data-clean/" + os.path.basename(src)).read()
        data += [(normalize(clean_src_content), sample["candidate"], label)]


    print(len(samples), "options")
    for sample in samples:
        print(sample)
    avg += len(samples)
    #print(len(samples))
    print(open(pre).read())
    print("----"*25)
    total += 1
    
    if len(samples) > 2:
        sys.exit(1)


print(avg/total)
print("Skipped", skipped, "total", total)
print(len(data))

random.shuffle(data)
train_split = .8
val_split = .1

train_end = math.floor(len(data)*train_split)
train_data = data[0:train_end]
eval_end = train_end+math.floor(len(data)*val_split)
eval_data = data[train_end:eval_end]
test_data = data[eval_end:]

print("train", len(train_data), "eval", len(eval_data), "test", len(test_data))

with open("train.csv", "w") as f:
    writer = csv.writer(f)
    writer.writerow(["MUT", "candidate", "label"])
    for data_row in train_data:
        writer.writerow(data_row)

with open("eval.csv", "w") as f:
    writer = csv.writer(f)
    writer.writerow(["MUT", "candidate", "label"])
    for data_row in eval_data:
        writer.writerow(data_row)

with open("test.csv", "w") as f:
    writer = csv.writer(f)
    writer.writerow(["MUT", "candidate", "label"])
    for data_row in test_data:
        writer.writerow(data_row)

#OK for the label I want a csv file...
#(input, candidate_check, label)
