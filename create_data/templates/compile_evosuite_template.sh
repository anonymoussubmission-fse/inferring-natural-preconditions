OUT_FOLDER=evosuite-tests
JUNIT=$PRECOND_LIBS/junit-4.12.jar
EVOSUITE=$PRECOND_LIBS/evosuite-1.2.0.jar
HAMCREST=$PRECOND_LIBS/hamcrest-core-1.3.jar
NEWJAR=$1
OLDJAR=$2
PACKAGEDIR=$3
CLASSJARS=$4
LIBJARS=$5
BASE=$(pwd)


cd $OUT_FOLDER 
cd $PACKAGEDIR

javac -cp $CLASSJARS:$JUNIT:$HAMCREST:$NEWJAR:$OLDJAR:$LIBJARS:$EVOSUITE *.java 
echo $BASE
cd $BASE
cd $OUT_FOLDER
jar cf tests.jar $PACKAGEDIR/*.class
