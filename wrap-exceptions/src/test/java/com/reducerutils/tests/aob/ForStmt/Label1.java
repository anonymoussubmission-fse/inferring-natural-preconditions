package com.reducerutils.tests.aob.ForStmt;

public class ForStmt1 {

    public boolean func(String[] foo, int bar) {
        for (int i = 0; i < 10; i++) {
            if (i < 0 || i >= foo.length)
                return true;
            String var_a = foo[i];
            if (bar + i + 2 < 0 || bar + i + 2 >= foo.length)
                return true;
            String var_b = foo[bar + i + 2];
        }
        return false;
    }
}
