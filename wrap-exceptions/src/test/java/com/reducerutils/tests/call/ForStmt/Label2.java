package com.reducerutils.tests.call.ForStmt;

public class ForStmt2 {

    public boolean func() {
        for (int i = 0; i < 10; i++) {
            int var_a;
            try {
                var_a = bar();
            } catch (java.lang.NullPointerException e) {
                return true;
            }
            boolean var_b;
            try {
                var_b = foo(var_a);
            } catch (java.lang.RuntimeException e) {
                return true;
            }
            assert (var_b);
        }
        return false;
    }
}
