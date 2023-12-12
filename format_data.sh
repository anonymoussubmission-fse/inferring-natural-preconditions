for f in ../jd-cli/data/*/Sample*_method_reduced.java; do
    echo $f
    java -jar ../google-java-format-1.13.0-all-deps.jar $f > /tmp/fmt.java
    cat /tmp/fmt.java
    mv /tmp/fmt.java $f
done
        
