package com.reducerutils.tests.call.ForStmt;

public class ForStmt1 {
    public boolean func() {
        for (int i = 0; i < 10; i++) {
            int var_a = bar();
            foo(var_a);
        }
        return false;
    }
}
