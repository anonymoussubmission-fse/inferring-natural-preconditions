import data_collection_consts as const
import data_collection_utils as utils
import subprocess as sp
import sys
import os

def package_target(sample, packaging_dir):
    print("Packaging")
    package_success = True
    sys.stdout.flush()

    newjar = sample.fname.replace(".java", ".jar")

    cmd = "jar cf " + newjar + " " + sample.package_name.replace(".","/") + "/" + sample.fname.replace(".java", ".class")
    print(cmd, "in directory", packaging_dir)

    try:
        sp.run(cmd, cwd=packaging_dir, stdout=sys.stdout, stderr=sys.stderr, shell=True, check=True)
    except sp.CalledProcessError as e:
        package_success = False

    utils.log(sample.log_file, "Packaging target method", package_success)
    return package_success


def compile_target_inner(compile_dir, path_to_target, jars, proj, og_class):
    oldjar_full_path, libjars_full_path, classjars_full_path = jars
    print("Compiling", path_to_target, "in", compile_dir)
    sys.stdout.flush()

    compilation_success = True
    err = None

    libs = ""
    for lib in libjars_full_path:
        libs += ":" + lib
    
    classlibs = ""
    for jar in classjars_full_path:
        classlibs += ":" + jar

    cmd = "javac -cp " + classlibs + ":" + oldjar_full_path + libs + " " + path_to_target
    #print(cmd)
    
    #compile_dir = sample_template_full_path.replace(template_fname, "")

    #print("in", compile_dir)

    output = sp.run(cmd, cwd=compile_dir, capture_output=True, shell=True) #, check=True) 
    #except sp.CalledProcessError as e:

    return output.stderr.decode()

def compile_target(sample, compile_dir, is_wrapped=False):

    err = compile_target_inner(compile_dir, os.path.join(sample.package_dir, sample.fname), sample.jars, sample.proj_name, sample.og_class_name)
    #print(err)

    oldjar_full_path, libjars_full_path, classjars_full_path = sample.jars


    compilation_success = True

    if "error" in err:
        print("COMPILATION FAILURE!")
        compilation_success = False

        if is_wrapped:
            err = "Wrapping failure"
            print(err)
        else:
            err_type = utils.process_error_reason(sample.og_class_name, classjars_full_path, err)

            if err_type == utils.CompilationError.og_class_compilation_failure:
                err = "original class didn't compile"
                print(err)
            elif err_type == utils.CompilationError.used_class_compilation_failure:
                err = "used class didn't compile"
                print(err)
            elif err_type == utils.CompilationError.template_failure:
                err = "template failure"
                print(err)
            elif err_type == utils.CompilationError.template_generic_failure:
                err = "template generic failure"
                print(err)
            elif err_type == utils.CompilationError.template_final_variable:
                err = "template final variable"
                print(err)
            elif err_type == utils.CompilationError.class_structure:
                err = "Class structure issue"
                print(err)

    sys.stdout.flush()

    if not err: 
        err = ""

    utils.log(sample.log_file, "Compilation", compilation_success, err)
    
    return compilation_success 

