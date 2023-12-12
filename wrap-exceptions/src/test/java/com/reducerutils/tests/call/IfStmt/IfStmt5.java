package com.reducerutils.tests.call.IfStmt;

public class IfStmt5 {
    public boolean func() {
        int var_a = bar();
        if (var_a == 0) {
            foo(1);
        } else {
            foo(0);
        }
        return false;
    }
}
