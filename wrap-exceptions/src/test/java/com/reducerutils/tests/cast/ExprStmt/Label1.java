package com.reducerutils.tests.cast.ExprStmt;

public class ExprStmt1 {

    public boolean mut(Object foo, int bar) {
        if (true) {
            if (!(foo instanceof Integer))
                return true;
            boolean b = (Integer) foo == bar;
        }
        return false;
    }
}
