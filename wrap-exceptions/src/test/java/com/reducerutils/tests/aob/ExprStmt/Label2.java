package com.reducerutils.tests.aob.ExprStmt;

public class ExprStmt2 {

    public boolean mut(Object[] foo, int bar) {
        if (true) {
            if (bar < 0 || bar >= foo.length)
                return true;
            foo[bar] = null;
            if (bar + 100 < 0 || bar + 100 >= foo.length)
                return true;
            foo[bar + 100] = null;
        }
        return false;
    }
}
