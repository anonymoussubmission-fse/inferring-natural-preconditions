package com.reducerutils.tests.aob.ForStmt;

public class ForStmt3 {
    public boolean mut(int[] foo, int bar) {
        for (int i=0; i<10; i+=foo[bar]) {
            assert(bar == foo[bar+i]);
        }

        return false;
    }
}
