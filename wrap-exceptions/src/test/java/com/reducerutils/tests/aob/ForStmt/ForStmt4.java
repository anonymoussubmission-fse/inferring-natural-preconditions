package com.reducerutils.tests.aob.ForStmt;

public class ForStmt4 {

    public boolean mut(int[] foo, int bar) {
        for (int i=foo[bar]; i<10; i++) {
            assert(bar == i + foo[bar]);
        }

        return false;
    }
}
