package com.reducerutils.tests.aob.ExprStmt;

public class ExprStmt1 {

    public boolean mut(Double[] foo, int bar) {
        if (true) {
            if (bar < 0 || bar >= foo.length)
                return true;
            foo[bar] = null;
        }
        return false;
    }
}
