PRECOND_HOME='/home/edinella/neural-testing/'
PRECOND_LIBS='$PRECOND_HOME/precondition-data-collection/precond-libs/'
SCRIPT='$PRECOND_HOME/precondition-data-collection/create_data/src/data_stats.py'
PY=/home/edinella/miniconda3/bin/python
for i in 5 7 8; do
    st=$(ssh edinella@ash0$i.seas.upenn.edu "export PRECOND_HOME=$PRECOND_HOME && export PRECOND_LIBS=$PRECOND_LIBS && $PY $SCRIPT")
    loc=$(echo $st |   sed -E "s/.*collected ([0-9]+) samples.*/\1/")
    echo $loc
    total=$(($total+loc))
done

echo
echo "total"
echo $total
#ssh edinella@ash07.seas.upenn.edu "export PRECOND_HOME=$PRECOND_HOME && export PRECOND_LIBS=$PRECOND_LIBS && $PY $SCRIPT"
#ssh edinella@ash05.seas.upenn.edu "export PRECOND_HOME=$PRECOND_HOME && export PRECOND_LIBS=$PRECOND_LIBS && $PY $SCRIPT"
