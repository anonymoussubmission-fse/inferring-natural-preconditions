package com.reducerutils.tests.cast.ExprStmt;

public class ExprStmt3 {

    public boolean mut(Object[] foo, int bar) {
        if (true) {
            if (!(foo[bar] instanceof String))
                return true;
            String var_a = (String) foo[bar];
            if (bar - 10 < 0)
                return true;
            Object[] var_b = new Object[bar - 10];
        }
        return false;
    }
}
