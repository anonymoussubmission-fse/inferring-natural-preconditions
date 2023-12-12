package com.reducerutils.tests.call.IfStmt;

public class IfStmt2 {

    public boolean func() {
        if (true) {
            int i = 0;
        } else {
            int var_a = bar();
            boolean var_b;
            try {
                var_b = foo(var_a);
            } catch (java.lang.NullPointerException e) {
                return true;
            }
            if (var_b) {
                int j = 0;
            }
        }
        return false;
    }
}
