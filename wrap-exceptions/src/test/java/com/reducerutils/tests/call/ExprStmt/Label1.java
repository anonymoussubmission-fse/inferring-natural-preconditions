package com.reducerutils.tests.call.ExprStmt;

public class ExprStmt1 {

    public boolean func() {
        if (true) {
            int var_a;
            try {
                var_a = bar();
            } catch (java.lang.NullPointerException e) {
                return true;
            }
            try {
                foo(var_a);
            } catch (java.lang.IndexOutOfBoundsException e) {
                return true;
            }
        }
        return false;
    }
}
