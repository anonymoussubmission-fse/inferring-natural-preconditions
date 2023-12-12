package com.reducerutils.tests.call.IfStmt;

public class IfStmt1 {

    public boolean func() {
        int var_a = bar();
        boolean var_b;
        try {
            var_b = foo(var_a);
        } catch (java.lang.NullPointerException e) {
            return true;
        }
        if (var_b) {
            int i = 0;
        }
        return false;
    }
}