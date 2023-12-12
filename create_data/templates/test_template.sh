#!/usr/bin/env bash

TMPDIR=<TMPDIR>
SRCFILE=<SRCFILE>
NEWJAR=<NEWJAR> 
OLDJAR=<OLDJAR> 
CLASSJARS=<CLASSJARS>
EVOFOLDER=<EVOJAR> 
PACKAGEDIR=<PACKAGEDIR>
LIBJARS=<LIBJARS>
EVOCLASS=<EVOCLASS> #org.json.foo_ESTest
EVOJAR=$PRECOND_LIBS/evosuite-1.2.0.jar
BASEDIR=$(pwd)
PACKAGENAME=$(echo $PACKAGEDIR | sed "s/\//\./g")
TIMESTAMP=$(date +"%H:%M:%S")

PREV=""
for w in $(echo $PACKAGEDIR | tr "/" " ") ; 
    do mkdir $PREV$w; 
    PREV=$PREV$w"/";
done 

cp $SRCFILE $PACKAGEDIR
cd $PACKAGEDIR
CLASSFILE=$(echo $SRCFILE | sed 's/\.java/\.class/g')
rm $CLASSFILE
javac -cp $CLASSJARS:$OLDJAR:$LIBJARS $SRCFILE
test -f $CLASSFILE
if [[ $? == 1 ]]; then
    exit 1
fi

cd $BASEDIR
cp -r evosuite-tests evosuite-tests-$TIMESTAMP

jar cf $NEWJAR $PACKAGENAME/${SRCFILE/".java"/".class"}  #org/json/foo.class

num_f=$(grep "false" $BASEDIR/$SRCFILE	| wc -l)
num_f_og=$(grep "false" $TMPDIR/$SRCFILE | wc -l)
echo $num_f
if [[ $num_f == 0 ]] && [[ $num_f_og > 0 ]]; then
    exit 1
fi

java -cp $CLASSJARS:$OLDJAR:$EVOFOLDER:$NEWJAR:$LIBJARS:$EVOJAR:$PRECOND_LIBS/junit-4.12.jar:$PRECOND_LIBS/hamcrest-core-1.3.jar org.junit.runner.JUnitCore $EVOCLASS
