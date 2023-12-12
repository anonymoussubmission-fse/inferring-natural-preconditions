from glob import glob
import javalang
import random

def find_end_line_number(node):
    """Finds end line of a node."""
    max_line = node.position.line

    def traverse(node):
        if not hasattr(node, "children"): 
            return
        for child in node.children:
            if child and hasattr(child, "position"):
                nonlocal max_line
                if child.position and child.position.line > max_line:
                    max_line = child.position.line
                    #return
            elif isinstance(child, list) and (len(child) > 0):
                for item in child:
                    traverse(item)

            traverse(child)

    traverse(node) 
    return max_line + 1

PREFIX = ""
LINUX_PREFIX = ""

OUT_DIR = "all-clean-data/"
KEY = "<?@#PRECONDITION?@#>"

samples = glob(PREFIX + "all-data/data/*/Sample*_method_reduced.java")
random.shuffle(samples)
for samplename in samples: #[0:25]:
    content = open(samplename).read()

    try:
        ast = javalang.parse.parse(content)
    except javalang.parser.JavaSyntaxError:
        print("couldn't parse", content)
        continue

    start, end = -1, -1
    for path, node in ast.filter(javalang.tree.MethodDeclaration):
        if node.name == "func": 
            start = node.position.line - 1
            end = find_end_line_number(node)
            print(start, end)


    if start == -1 or end == -1:
        print("no method named func")
        continue

    lines = content.split("\n")
    lines = lines[0:start] + [KEY] + lines[end:]

    new_content = "\n".join(lines)

    #print(new_content)

    out_file = samplename.replace("all-data", OUT_DIR).replace("_reduced", "").replace("/data", "")
    print("writing to", out_file)
    with open(out_file, "w") as f:
        f.write(new_content)


    
    #call a method that does...
    '''
        replace func() method with something like <$#@PRECONDITION$#@> so we can easily swap in the predicted method later
        save it as Sample*_method.java  (remove the _reduced) tag
    '''

