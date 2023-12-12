#1. create a lib folder and move the non-main jar
#2. compile all of the java files into class files


if [ -z "$1" ]; then
    echo "USAGE <PATH_TO_PROJECTS> <PROJECT_NAME>"
    exit 1
fi


if [ -z "$2" ]; then
    echo "USAGE <PATH_TO_PROJECTS> <PROJECT_NAME>"
    exit 1
fi

PREFIX=$1
PROJ=$2
TOTAL_BROKEN=0
TOTAL_OK=0

cd $PREFIX/$PROJ

if [ -d "class-jars" ]; then
  echo 
  #do nothing
else
  mkdir class-jars
  #echo "does not exist";
fi



JAR=$(echo $PROJ | sed 's/^[0-9]*_//')".jar"
for f in $(find . -name "*.java" | grep -v _method.java); do 
    #CLASS=$(echo $f | sed "s+$PREFIX\/+ +")
    #echo $f

    #JAR=apbsmem.jar
    LIBS=""
    if [ -z "$(ls libs/*.jar)" ]; then
       echo "No libs"
    else
       LIBS=$(ls libs/*.jar)
       LIBS=$(echo $LIBS | sed "s+ +:+g")
    fi

    OUTPUT=$(javac -cp $JAR:$LIBS $f 2>&1)

    if [ -z $(echo $OUTPUT | grep "error") ]; then
        TOTAL_OK=$(( $TOTAL_OK + 1 ))
    else 
        #echo $OUTPUT
        #break
        TOTAL_BROKEN=$(( $TOTAL_BROKEN + 1 ))
        echo "broken $TOTAL_BROKEN, ok $TOTAL_OK"
        continue
    fi


    echo "broken $TOTAL_BROKEN, ok $TOTAL_OK"


    CLASSFILES=$(echo $f | sed "s+\.java+*\.class+g")

    JARNAME=$(echo $f | sed "s/^\.\///g")
    JARNAME=$(echo $JARNAME | sed "s+\/+\-+g")
    JARNAME=$(echo $JARNAME | sed "s+\.java+\.jar+g")

    CMD="jar cvf $JARNAME $CLASSFILES"
    echo $CMD
    $CMD
    mv $JARNAME class-jars/

    echo
    echo

done
