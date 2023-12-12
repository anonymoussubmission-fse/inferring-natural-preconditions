package com.reducerutils.tests.call.ForStmt;

public class ForStmt1 {

    public boolean func() {
        for (int i = 0; i < 10; i++) {
            int var_a;
            try {
                var_a = bar();
            } catch (java.lang.NullPointerException e) {
                return true;
            }
            foo(var_a);
        }
        return false;
    }
}