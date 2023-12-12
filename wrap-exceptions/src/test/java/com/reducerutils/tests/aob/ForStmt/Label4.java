package com.reducerutils.tests.aob.ForStmt;

public class ForStmt4 {

    public boolean mut(int[] foo, int bar) {
        if (bar < 0 || bar >= foo.length)
            return true;
        for (int i = foo[bar]; i < 10; i++) {
            if (bar < 0 || bar >= foo.length)
                return true;
            assert (bar == i + foo[bar]);
        }
        return false;
    }
}
