if [ -z "$1" ]; then
    echo "USAGE <PROJECT_NAME> <LAST_SAMPLE_NUM>"
    exit 1
fi

if [ -z "$2" ]; then
    echo "USAGE <PROJECT_NAME> <LAST_SAMPLE_NUM>"
    exit 1
fi

if [ -z "$PRECOND_HOME" ]; then
	echo "SET YOUR PRECOND_HOME"
	exit 1
fi

if [ -z "$PRECOND_LIBS" ]; then
	echo "SET YOUR PRECOND_LIBS"
	exit 1
fi


PROJ=$1
LAST_SAMPLE_NUM=$2
IS_SF100=$3

SF100=$PRECOND_LIBS/SF100/
PREFIX=$PRECOND_HOME/projects/
JDCLI=$PRECOND_LIBS/jd-cli.jar
TEMPLATE_DIR=$PRECOND_HOME/precondition-data-collection/template-creation/

if [ ! -z "$IS_SF100" ]; then
    cp $SF100/$PROJ/*.jar $PREFIX/$PROJ/
    rm $PREFIX/$PROJ/\.*

    cp -r $SF100/$PROJ/lib/ $PREFIX/$PROJ/libs/
fi


cd $PREFIX/$PROJ
echo "decompiling"
STRIPPED_PROJ=$(echo $PROJ | sed 's/.*_//')
echo "stripped proj $STRIPPED_PROJ"

java -jar $JDCLI $STRIPPED_PROJ.jar 

jar xvf $STRIPPED_PROJ.src.jar 

echo $LAST_SAMPLE_NUM

bash $TEMPLATE_DIR/create_templates.sh  $PREFIX $TEMPLATE_DIR/target/dataset-1.0-SNAPSHOT-shaded.jar $PROJ $LAST_SAMPLE_NUM

bash $TEMPLATE_DIR/compile_all_original.sh $PREFIX $PROJ

#copy from SF100
#rm evosuite-files
#mv lib libs
#java -jar ../jd-cli.jar a4j.jar 
#jar xvf a4j.src.jar 
#find the last smaple number
#run create_templates.sh
#bash compile_all_original.sh /home/edinella/neural-testing/jd-cli/ 2_a4j
