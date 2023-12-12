package com.reducerutils.tests.call.WhileStmt;

public class WhileStmt1 {
    public boolean func() {
        int a = 5;
        while (a < 10) {
            int var_b = baz();
            int var_c = baz();
            a = var_b + var_c;
        }
        int var_a = a;
        return false;
    }

}
