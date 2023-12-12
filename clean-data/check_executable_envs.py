from glob import glob
from multiprocessing import Pool
import subprocess as sp
import os

PREFIX = "/home/edinella/neural-testing/"
ENV_DIR = PREFIX + "excutable-environments/"

def run_main(sample):
    return (sp.call(["bash " + sample], shell=True, cwd=os.path.dirname(sample)), sample)
    
def main():
    broken, ok = 0,0
    samples = glob(os.path.join(ENV_DIR, '*', "RunTests*.sh"))
        #print(sample)

    
    with Pool(256) as p:
        rets = p.map(run_main, samples)
    
    ok = len([ret for ret in rets if ret[0] == 0])
    broken = len([ret for ret in rets if not ret[1] == 0])
    #print(ok, broken)

    with open("failures.txt", "w") as f:
        f.write("\n".join([ret[1] for ret in rets if not ret[0] == 0]))

if __name__ == '__main__':
    main()

