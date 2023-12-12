#mvn clean install

if [ -z "$1" ]; then
    echo "Usage: <PROJECT_PREFIX> <TEMPLATE_CREATION_JAR_PATH> <PROJECT_NAME> <SAMPLE_NUM>"
    exit 1
fi

if [ -z "$2" ]; then
    echo "Usage: <PROJECT_PREFIX> <TEMPLATE_CREATION_JAR_PATH> <PROJECT_NAME> <SAMPLE_NUM>"
    exit 1
fi

if [ -z "$3" ]; then
    echo "Usage: <PROJECT_PREFIX> <TEMPLATE_CREATION_JAR_PATH> <PROJECT_NAME> <SAMPLE_NUM>"
    exit 1
fi

if [ -z "$4" ]; then
    echo "Usage: <PROJECT_PREFIX> <TEMPLATE_CREATION_JAR_PATH> <PROJECT_NAME> <SAMPLE_NUM>"
    exit 1
fi


PREFIX=$1
JAR=$2
PROJECT=$3
SAMPLENUM=$4
TOTAL_BROKEN=0

for FULL_CLASS in $(find $PREFIX/$PROJECT/ -name "*.java"); do
    CLASS=$(echo $FULL_CLASS | sed "s+$PREFIX\/+ +")
    CLASS=$(echo $CLASS | sed "s+$PROJECT\/+ +")

    if echo "$CLASS" | grep "_method.java"; then
        echo "SAMPLE FILE.... skipping ";
        break
    fi

    CMD="java -jar $JAR $PREFIX $PROJECT $CLASS $SAMPLENUM"
    echo $CMD
    OUTPUT=$($CMD)

    if [ $? -eq 1 ]; then
        echo $OUTPUT
        exit
    fi

    echo $OUTPUT
    IFS=',' read -r a b <<< "$OUTPUT"

    BROKEN=$a
    SAMPLENUM=$b

    TOTAL_BROKEN=$(( $TOTAL_BROKEN + $BROKEN ))

    echo "broken: $TOTAL_BROKEN"
    echo "next sample: $SAMPLENUM"

    echo
    echo
done
