package com.reducerutils.tests.call.WhileStmt;

public class WhileStmt2 {
    public boolean func() {
        int a = 5;
        while (true) {
            int var_b = bar();
            boolean var_c = foo(var_b);
            if (!(var_c)) {
                break;
            }
            a += baz();
        }
        int var_a = a;
        return false;
    }
}
