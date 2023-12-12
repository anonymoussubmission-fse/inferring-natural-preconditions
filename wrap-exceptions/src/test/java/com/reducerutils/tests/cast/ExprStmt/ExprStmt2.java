package com.reducerutils.tests.cast.ExprStmt;

public class ExprStmt2 {

    public boolean mut(Object foo, Object bar) {
        if (true) {
            String var_a = ((String) foo).concat((String) bar);
        }

        return false;
    }
}
