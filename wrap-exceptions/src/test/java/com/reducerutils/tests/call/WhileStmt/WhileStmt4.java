package com.reducerutils.tests.call.WhileStmt;

public class WhileStmt4 {
    public boolean func() {
        int a = 5;
        while (true) {
            int var_b = bar();
            boolean var_c = foo(var_b);
            if (!(var_c)) {
                break;
            }
            a += baz();
            while (true) {
                int var_d = bar();
                if (!(a < 42 + var_d)) {
                    break;
                }
                boolean var_e = foo(100);
                if (var_e) {
                    continue;
                }
            }
        }
        int var_a = a;
        return false;
    }
}