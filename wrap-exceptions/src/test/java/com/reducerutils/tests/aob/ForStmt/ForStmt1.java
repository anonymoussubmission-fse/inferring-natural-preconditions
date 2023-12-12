package com.reducerutils.tests.aob.ForStmt;

public class ForStmt1 {
    public boolean func(String[] foo, int bar) {
        for (int i = 0; i < 10; i++) {
            String var_a = foo[i];
            String var_b = foo[bar + i + 2];
        }
        return false;
    }
}
