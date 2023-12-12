package com.reducerutils.tests.call.ForStmt;

public class ForStmt3 {

    public boolean func() {
        for (int i = 0; true; i++) {
            int var_a;
            try {
                var_a = bar();
            } catch (java.lang.NullPointerException e) {
                return true;
            }
            if (!(i < var_a))
                break;
            boolean var_b;
            try {
                var_b = foo(i);
            } catch (java.lang.NullPointerException e) {
                return true;
            }
            assert (var_b);
        }
        return false;
    }
}
