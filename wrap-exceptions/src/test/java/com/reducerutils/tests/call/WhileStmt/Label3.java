package com.reducerutils.tests.call.WhileStmt;

public class WhileStmt3 {

    public boolean func() {
        int a = 5;
        while (true) {
            int var_b;
            try {
                var_b = bar();
            } catch (java.lang.NullPointerException e) {
                return true;
            }
            boolean var_c = foo(var_b);
            if (!(var_c)) {
                break;
            }
            if (a > 10) {
                try {
                    a += baz();
                } catch (java.lang.ParseException e) {
                    return true;
                } catch (java.lang.RuntimeException e) {
                    return true;
                }
            } else {
                a += eliz();
            }
            a += 15;
        }
        int var_a = a;
        return false;
    }
}
