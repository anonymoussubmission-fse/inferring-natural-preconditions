package com.reducerutils.tests.call.WhileStmt;

public class WhileStmt2 {

    public boolean func() {
        int a = 5;
        while (true) {
            int var_b = bar();
            boolean var_c;
            try {
                var_c = foo(var_b);
            } catch (java.lang.ParseException e) {
                return true;
            }
            if (!(var_c)) {
                break;
            }
            try {
                a += baz();
            } catch (java.lang.RuntimeException e) {
                return true;
            }
        }
        int var_a = a;
        return false;
    }
}
