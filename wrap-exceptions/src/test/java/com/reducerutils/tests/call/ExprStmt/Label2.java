package com.reducerutils.tests.call.ExprStmt;

public class ExprStmt2 {

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
        } else {
            int var_b;
            try {
                var_b = bar();
            } catch (java.lang.IndexOutOfBoundsException e) {
                return true;
            }
            foo(var_b);
        }
        return false;
    }
}
