from glob import glob
import subprocess as sp

PREFIX = ""
LINUX_PREFIX = ""
for samplename in glob(PREFIX + "all-data/data/*/Sample*_method_reduced.java"):
    print(samplename)
    sp.call([f"java -jar target/clean-data-1.0-SNAPSHOT-shaded.jar {samplename}"], shell=True)
