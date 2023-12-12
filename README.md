# Requirements
- Java 8 (You need to use Java 8!!)
- Python 3.x
- mvn
- Clone this repo :)

First, you'll need to build some java projects..
`cd precondition-data-collection`

1. Excpetion wrapping:    
`cd wrap-exceptions`   
`mvn clean install -DskipTests`

2. Evosuite cleaning:    
`cd clean-evosuite-tests`    
`mvn clean install`    

3. Template creation:    
`cd template-creation`   
`mvn clean install -DskipTests`


4. Download necessary dependencies:
put them all in the same folder and set the environment variable $PRECOND_LIBS

Download and Install astyle
https://astyle.sourceforge.net/
(only put the built binary in the PRECOND_LIBS folder)

perses release
https://github.com/uw-pluverse/perses/releases/tag/v1.4

Download EvoSuite and its necessary libs: (evosuite-1.2.0.jar, junit-4.12.jar and hamcrest-core-1.3.jar)
https://mvnrepository.com/artifact/junit/junit/4.12
https://github.com/EvoSuite/evosuite/releases
https://mvnrepository.com/artifact/org.hamcrest/hamcrest-core/1.3

Download the java decompiler:
https://github.com/intoolswetrust/jd-cli/releases
Download jd-cli-1.2.0-dist.tar.gz  and unzip it. You only need the .jar file you can delete the rest


set PRECOND_HOME to the directory that contains this repo

# Setup

## Data Setup:

1. Setup the directory of projects you would like to generate preconditions for:

If your project is not from SF100, just put it in its own folder {num}_{name} that has a jar file {name}.jar with the src and a libs/dependencies. 
$PRECOND_HOME/projects/{project}/project.jar
   Include dependencies in:
    $PRECOND_HOME/projects/{project}/libs/{lib.jar}

   If you would like to use SF100 files (I do), you'll need to download them: http://www.evosuite.org/files/SF100-20120316.tar.gz
   untar it and put it in $PRECOND_LIBS
   

# Data Collection:

1. First you'll need to run: `python setup_template_creation.py`    
Requiremenets:    
First, create a `projects.txt` file for setup to read from

cd $PRECOND_HOME/projects/ && ls -l > projects.txt
mv projects.txt $PRECOND_LIBS/

2. Now, lets generate preconditions for those templates
   `cd create_data && python setup.py install`
