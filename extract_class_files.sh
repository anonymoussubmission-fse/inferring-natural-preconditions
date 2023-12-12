BASE=$(pwd)
for f in ../jd-cli/*; do
	if [ -d $f ]; then
		echo $f
		DIRNAME=$(basename $f)
		echo "going to $DIRNAME"
		cd $f
		echo $pwd
		PROJNAME=$(echo $DIRNAME | cut -d "_" -f 2)
		jar xf $PROJNAME.jar
		cd $BASE
		#jar xvf 
	fi
done
