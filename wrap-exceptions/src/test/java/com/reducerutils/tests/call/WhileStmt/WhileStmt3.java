package com.reducerutils.tests.call.WhileStmt;

public class WhileStmt3 {
    public boolean func() {
        int a = 5;
        while (true) {
            int var_b = bar();
            boolean var_c = foo(var_b);
            if (!(var_c)) {
                break;
            }
            if (a > 10) {
                a += baz();
            } else {
                a += eliz();
            }
            a += 15;
        }
        int var_a = a;
        return false;
    }
}
