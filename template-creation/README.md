`bash create_templates.sh <project> <startingSampleNum>`

`mvn clean install`
remove old samples:
`find ../jd-cli/35_corina/ -name "*Sample*.java"   | grep "_method" | xargs rm`
`java -jar target/dataset-1.0-SNAPSHOT-shaded.jar <prefix> <project> <classPath> <startingSampleNum>`
