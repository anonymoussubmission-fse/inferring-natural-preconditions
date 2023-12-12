#!/usr/bin/env bash

PRE_WRAPPER_FILE=<PRE_WRAPPER_FILE>
TARGET_FILE=$(basename $PRE_WRAPPER_FILE)
PRED_PRE_FILE=<PRED_PRE_FILE>
NEWJAR=precondition.jar
OG_PROJ_JAR=<OG_PROJ_JAR>
CLASSJARS=<CLASSJARS>
TESTJAR=<TESTJAR> 
PACKAGEDIR=<PACKAGEDIR>
LIBJARS=<LIBJARS>
EVOCLASS=<EVOCLASS> #org.json.foo_ESTest
EVOJAR=$ENV_PREFIX/evosuite-standalone-runtime-1.2.0.jar

cd $WORKDIR
PREV=""
for w in $(echo $PACKAGEDIR | tr "/" " ") ; 
    do mkdir $PREV$w; 
    PREV=$PREV$w"/";
done 

DIR=$(dirname $OG_PROJ_JAR)
s_num=$(basename $PRE_WRAPPER_FILE .java)
echo $DIR
cp ${DIR}/${s_num}*ESTest*.class $PACKAGEDIR


cp $PRE_WRAPPER_FILE $PACKAGEDIR
cd $PACKAGEDIR


PRECONDITION=`cat $PRED_PRE_FILE`
PRECONDITION=`printf "%q " $PRECONDITION`
sed -i "s/<?@#PRECONDITION?@#>/${PRECONDITION}/" $TARGET_FILE

javac -cp $CLASSJARS:$OG_PROJ_JAR:$LIBJARS $TARGET_FILE
cd $WORKDIR
jar cf $NEWJAR $PACKAGEDIR/${TARGET_FILE/".java"/".class"}  #org/json/foo.class

java -cp $CLASSJARS:$OG_PROJ_JAR:$TESTJAR:$NEWJAR:$LIBJARS:$EVOJAR:$ENV_PREFIX/junit-4.12.jar:$ENV_PREFIX/hamcrest-core-1.3.jar org.junit.runner.JUnitCore $EVOCLASS
