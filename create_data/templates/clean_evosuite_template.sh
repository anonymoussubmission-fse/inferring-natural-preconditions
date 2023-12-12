OUT_FOLDER=evosuite-tests
CLEANJAR=$PRECOND_HOME/precondition-data-collection/clean-evosuite-tests/target/clean-evo-1.0-SNAPSHOT-shaded.jar

PACKAGE_DIR=$1
EVOFILE=$2

BASE=$(pwd)

cd $OUT_FOLDER 
cd $PACKAGE_DIR
java -jar $CLEANJAR $EVOFILE > test.java
mv test.java $EVOFILE
