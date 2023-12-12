_dir=$1
clean_dir=$2
log=$3

cd $_dir 
timeout 240 ~/apache-ant-1.9.16/bin/ant -f /home/edinella/neural-testing/defects4j/framework/projects/defects4j.build.xml -Dd4j.home=/home/edinella/neural-testing/defects4j -Dd4j.dir.projects=/home/edinella/neural-testing/defects4j/framework/projects -Dbasedir=$clean_dir -DOUTFILE=$log -Dd4j.test.dir=$_dir -Dd4j.test.include=*.java run.gen.tests 
#2>&1
