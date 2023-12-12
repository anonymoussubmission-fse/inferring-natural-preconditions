import os
import json
import data_collection_consts as const
import data_collection_utils as util
import subprocess as sp 
import sys

#Returns None, err if wrapping fails
def wrap_exception(sampleFileName, wraps):
    err = ""

    print("wrapping", sampleFileName, "with", wraps)

    data_filename = os.path.join(os.path.dirname(sampleFileName), "wrap_settings.json")

    with open(data_filename, "w") as f:
        json.dump({"wraps": wraps}, f, indent=4)

    print("wrap settings", wraps)
    args = " ".join([sampleFileName, data_filename])
    print("java -jar " + const.WRAPPING_JAR + " " + args)
    try:
        output = sp.check_output("java -jar " + const.WRAPPING_JAR + " " + args, stderr=sp.DEVNULL, shell=True).decode()
    except Exception as e:
        print("exception in wrapper")
        print(e.output)
        sys.stdout.flush()

        err = "WRAPPING FAILED"
        return None, err
        #sys.exit(1)
        #return None

    if "WRAPPING FAILED" in output:
        print(output)
        err = "WRAPPING FAILED"
        return None, err


    return output, err


def create_wrap_json(traces, sample_fname):
    wraps = []
    trace_num = 0
    err = None

    for trace in traces:
        exception_type = trace.exception_type
        location_line = trace.location.lineno 

        lineno = int(location_line)

        _class = trace.location._class 

        trace_num += 1

        if _class + ".java" == sample_fname:

            #Example: at corina.Range.span(Range.java:62) extracts out 62 

            if exception_type == "java.lang.NullPointerException":
                wrap_type = "NPE"
            elif exception_type == "java.lang.ArrayIndexOutOfBoundsException":
                wrap_type = "AOB"
            elif exception_type == "java.lang.ClassCastException":
                wrap_type = "cast"
            elif exception_type == "java.lang.NegativeArraySizeException":
                wrap_type = "negArray"
            elif exception_type == "java.lang.ArithmeticException": 
                wrap_type = "divZero"
            else:
                print("UNKNOWN WRAP TYPE", lineno, exception_type)
                err = "Unknown wrap type " + exception_type

                return None, None, err
        else:
            #wrap the line in a try catch
            wrap_type = "call"                

            '''
            if not lineno: 
                print("BAD CRASH.. COULDNT GET THE LINE NUMBER")
                err = "Could not get line number"
                return None, None, err

            '''
        wraps.append({"type": wrap_type, "lineNo": lineno, "exceptionType": exception_type})

    return wraps, trace_num, err

# traces: list of Trace objects
def wrap(traces, sample):
    success = True

    wraps, trace_num, err = create_wrap_json(traces, sample.fname)

    if err: 
        success = False
        util.log(sample.log_file, "Wrapping", success, err)

        return None, None, err

    wrapped, err = wrap_exception(sample.working_sample, wraps)

    #if "OK" in traces: return #NO IMPLICIT EXCEPTIONS 
    '''
    num_failures = int(traces[traces.find('Failures:')+9:].strip())
    try:
        assert trace_num == num_failures
    except AssertionError as e:
        print(e)
        print("number of failures from trace", num_failures, "is not equal to the count", trace_num)
    '''
    if err:
        success = False
        util.log(sample.log_file, "Wrapping", success, err)

    if wrapped == None:
        print("ERROR with wrapping")
        sys.stdout.flush()
        #return

    return wrapped, wraps, err

