import argparse

# Create the argument parser
parser = argparse.ArgumentParser(description='Description of your script.')

# required arguments
parser.add_argument('num_threads', help='number of threads for parallelization of data collection') 

# Turn off various steps of the pipeline
parser.add_argument('--dont_gen_inputs', action='store_true',  default=False, help='turns off evosuite generation (usually used when the tests have already been generated)')
parser.add_argument('--only_reduce', action='store_true',  default=False, help='turns off everything but reduction')
parser.add_argument('--print_stats', action='store_true',  default=False, help='turns off evosuite generation (usually used when the tests have already been generated)')

#Specify a single project to run collection on
parser.add_argument('--project', default=None, help='specify a single project to run collection on')
parser.add_argument('--project_file', default=None, help='specify a file with a project on each line to run collection on')

#Specify specific samples to run collection on
parser.add_argument('--sample_file', default=None, help='specify a file with a data point on each line to run collection on')

args = parser.parse_args()

