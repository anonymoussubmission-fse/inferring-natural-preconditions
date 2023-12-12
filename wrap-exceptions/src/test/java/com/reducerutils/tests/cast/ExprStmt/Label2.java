package com.reducerutils.tests.cast.ExprStmt;

public class ExprStmt2 {

    public boolean mut(Object foo, Object bar) {
        if (true) {
            if (!(foo instanceof String))
                return true;
            if (!(bar instanceof String))
                return true;
            String var_a = ((String) foo).concat((String) bar);
        }
        return false;
    }
}
