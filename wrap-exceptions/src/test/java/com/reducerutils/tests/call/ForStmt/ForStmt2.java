package com.reducerutils.tests.call.ForStmt;

public class ForStmt2 {
    public boolean func() {
        for (int i = 0; i < 10; i++) {
            int var_a = bar();
            boolean var_b = foo(var_a);
            assert (var_b);
        }
        return false;
    }
}
