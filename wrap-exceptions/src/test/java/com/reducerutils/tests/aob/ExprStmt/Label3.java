package com.reducerutils.tests.aob.ExprStmt;

public class ExprStmt3 {

    public boolean mut(Object[] foo, int bar) {
        if (true) {
            if (bar < 0 || bar >= foo.length)
                return true;
            String var_a = (String) foo[bar];
            if (bar - 10 < 0)
                return true;
            Object[] var_b = new Object[bar - 10];
        }
        return false;
    }
}
