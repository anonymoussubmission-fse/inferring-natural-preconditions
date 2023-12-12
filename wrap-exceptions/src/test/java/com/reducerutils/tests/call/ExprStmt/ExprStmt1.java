package com.reducerutils.tests.call.ExprStmt;

public class ExprStmt1 {
    public boolean func() {
        if (true) {
            int var_a = bar();
            foo(var_a);
        }
        return false;
    }
}
