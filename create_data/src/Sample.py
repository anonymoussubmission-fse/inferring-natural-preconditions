import data_collection_consts as const
import data_collection_utils as utils
import os
import re
import json

class Sample():
    '''
        - full_path
        - fname: basename of full_path
        - fully_qualified_class: (corina.Graph.Sample450)
        - evo_file: basename of the ESTest file
        - evo_class: fully qualified evo_file (corina.graph.foo.evo_file)
        - evo_jar: working_dir/evosuite-tests/tests.jar
        - sample_num: the _id
        - workingdir: (old tmp dir /id/)
        - working_sample: workingdir/fname
        - proj_name: basename (35_corina)
        - proj_dir: base_dir/proj_name
        - package_name: corina.graph.foo
        - package_dir: corina/graph/foo
        - meta_fname: Sample{_id}_method.json
        - meta_file: full path to the metadata file
        - data_sample_path: Where to put the final reduced file
        - data_sample_reduced: The final reduced file
        - log_file: /workingdir/_id/log.txt
        - og_class_name: "package.Range.java"
        - og_class_jar: package-Range.jar
        - jars: oldjar_full_path, libjars_full_path, classjars_full_path
        - newjar: proj_base_dir/Sample{_id}_method.jar (in proj base dir)
        - working_newjar: workingdir/Sample{_id}_method.jar
    '''

    def __init__(self, full_path):
        self.full_path = full_path
        self.fname = os.path.basename(full_path)

        self.sample_num = re.search("Sample([0-9]+)_method\.java", self.fname).group(1)
        self.workingdir = os.path.join(const.WORKING_DIR, self.sample_num)
        self.working_sample = os.path.join(self.workingdir, self.fname)
        
        self.proj_name = full_path.replace(const.BASE_DIR, "").split(os.sep)[0]
        self.proj_dir = os.path.join(const.BASE_DIR, self.proj_name)

        local_path_to_template_sample = os.path.dirname(full_path.replace(const.BASE_DIR, "").replace(self.proj_name, "")).strip("/")
        self.package_name = local_path_to_template_sample.replace("/",".")
        self.package_dir = self.package_name.replace(".","/")

        self.fully_qualified_class = self.package_name + "." + self.fname.replace(".java", "")

        self.evo_file = self.fname.replace(".java", "_ESTest.java")
        self.evo_class = self.package_name + "." + self.evo_file.strip(".java")
        self.evojar = os.path.join(self.workingdir, "evosuite-tests", "tests.jar")

        self.meta_fname = self.fname.replace(".java", ".json")
        self.meta_file = os.path.join(const.BASE_DIR, "metadata", self.proj_name, self.meta_fname)

        if not os.path.exists(const.DATA_OUT):
            os.mkdir(const.DATA_OUT)
        
        data_dir = os.path.join(const.DATA_OUT, self.proj_name)
        if not os.path.exists(data_dir):
            os.mkdir(data_dir)

        self.data_sample_path = os.path.join(data_dir, self.fname)

        self.data_sample_reduced = os.path.join(data_dir, self.fname.replace(".java", "_reduced.java"))

        self.log_file = os.path.join(const.WORKING_DIR, self.sample_num, "log.txt")

        og_class_with_package_prefix = json.load(open(self.meta_file))["Class"]
        self.og_class_name = os.path.splitext(og_class_with_package_prefix)[0]

        self.og_class_jar = self.package_dir.replace("/", "-") + "-" + os.path.basename(self.og_class_name.replace(".","/"))

        self.newjar = os.path.join(const.BASE_DIR, self.proj_name, f"Sample{self.sample_num}_method.jar")
        self.working_newjar = os.path.join(const.WORKING_DIR, self.sample_num, f"Sample{self.sample_num}_method.jar")

        self.jars = utils.get_jars(self.proj_name)
        oldjar_full_path, libjars_full_path, classjars = self.jars

        
        try:
            assert oldjar_full_path
        except AssertionError as e:
            print("NO ORIGINAL PROJECT JAR!") 
            raise e
            #elif not classjars:
            #    return
            #    #print("NO CLASS JARS!")
            #    #raise e


    def __str__(self):
        return f"Sample: {self.sample_num}, {self.package_name}, {self.proj_name}"
