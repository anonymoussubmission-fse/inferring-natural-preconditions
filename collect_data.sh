SERVERS=("05" "07" "08" "09" "10")
#SERVERS=("07" "08" "09" "10")


for server in ${SERVERS[@]}
do
    scp -r edinella@ash$server.seas.upenn.edu:/home/edinella/neural-testing/jd-cli/data/ ash$server-data
done

for server in ${SERVERS[@]}
do 
    cp -r ash$server-data/ all-data/
done
