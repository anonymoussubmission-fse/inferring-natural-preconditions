package com.reducerutils.tests.aob.ExprStmt;

public class ExprStmt1 {

    public boolean mut(Double[] foo, int bar) {
        if (true) {
            foo[bar] = null;
        }

        return false;
    }
}
