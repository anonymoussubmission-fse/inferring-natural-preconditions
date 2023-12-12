package com.reducerutils.tests.call.IfStmt;

public class IfStmt1 {
    public boolean func() {
        int var_a = bar();
        boolean var_b = foo(var_a);
        if (var_b) {
            int i = 0;
        }
        return false;
    }

}
