PROJ=$1
if [ -z "$1" ]; then
    echo "USAGE <PROJECT_NAME>"
    exit 1
fi


LAST_SAMPLE=$(basename $(find $PRECOND_HOME/projects/$PROJ -name "Sample*_method.java" | sort | tail -1))
LAST_SAMPLE_NUM=$(echo $LAST_SAMPLE | sed -E "s/Sample([0-9]+)_method.java/\1/" | awk '{print $1+1}')

echo $LAST_SAMPLE_NUM
