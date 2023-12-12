package com.reducerutils.tests.call.WhileStmt;

public class WhileStmt1 {

    public boolean func() {
        int a = 5;
        while (a < 10) {
            int var_b;
            try {
                var_b = baz();
            } catch (java.lang.ParseException e) {
                return true;
            }
            int var_c;
            try {
                var_c = baz();
            } catch (java.lang.RuntimeException e) {
                return true;
            }
            a = var_b + var_c;
        }
        int var_a = a;
        return false;
    }
}
