package com.reducerutils.tests.aob.ExprStmt;

public class ExprStmt2 {

    public boolean mut(Object[] foo, int bar) {
        if (true) {
            foo[bar] = null;
            foo[bar + 100] = null;
        }

        return false;
    }
}
