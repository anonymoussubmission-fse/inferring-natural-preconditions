package com.reducerutils.tests.call.IfStmt;

public class IfStmt4 {

    public boolean func() throws Exception {
        int var_a = bar();
        if (var_a == 0) {
            try {
                foo(1);
            } catch (java.lang.NullPointerException e) {
                return true;
            }
        }
        int var_b;
        try {
            var_b = bar();
        } catch (java.lang.NullPointerException e) {
            return true;
        }
        if (var_b == 100) {
            return true;
        }
        return false;
    }
}
