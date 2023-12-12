for F_SAMPLE in $(ls ../../jd-cli/data/60_sugar/Sample*_method.java)
do
    PROJ=$(basename $(dirname $F_SAMPLE)) 
    #PROJ="92_jcvi-javacommon"
    WRAP=$(echo $F_SAMPLE | sed 's/Sample/WrapLog/')
    WRAP=$(echo $WRAP | sed 's/\.java/\.json/')

    echo "================================="
    echo $F_SAMPLE
    #echo $PROJ
    #echo $WRAP
    #echo $(cat $WRAP)

    FOO=$(cat $WRAP | python -c "import json; import sys; obj = json.load(sys.stdin); print(\"\n\".join([str(wrap) for wrap in obj]))")

    if [ -z "$FOO" ]
    then
	    echo "no wraps"
	    echo ""
	    continue;
    fi

    #echo $(cat $F_SAMPLE) > "tmp.java"

    echo $FOO
    CNT=1
    while IFS= read -r line;
    do
	echo "{\"wraps\": $line}" > "tmp.json"
	echo $(cat "tmp.json")

	java -cp target-inline/wrap-exceptions-1.0-SNAPSHOT-shaded.jar com.reducerutils.WrapExceptions \
		$F_SAMPLE "tmp.json" ../../jd-cli/$PROJ

	#echo $OUT > "tmp.java"

	CNT=$((CNT+1))

    done <<< "$FOO"

    #echo $CNT
    #if [ $CNT -gt 2 ]
    #then
    #	break
    #fi
 
    echo ""
    #For each wrap in the wraps array, 
    #	create an object {"wraps": WRAP}

    
    #test-inline/Sample108176_method.java test-inline/wrap_new.json project/ 

    #break
done
