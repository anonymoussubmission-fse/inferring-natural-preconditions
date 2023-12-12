package com.reducerutils.tests.call.ForStmt;

public class ForStmt4 {

    public boolean func() {
        boolean _var_is_first_itr = false;
        for (int i = 0; i < 10; ) {
            if (!_var_is_first_itr) {
                _var_is_first_itr = true;
            } else {
                int var_a;
                try {
                    var_a = bar();
                } catch (java.lang.NullPointerException e) {
                    return true;
                }
                i += var_a;
            }
            if (!(i < 10))
                break;
            boolean var_b;
            try {
                var_b = foo(100);
            } catch (java.lang.RuntimeException e) {
                return true;
            }
            assert (var_b);
        }
        return false;
    }
}
