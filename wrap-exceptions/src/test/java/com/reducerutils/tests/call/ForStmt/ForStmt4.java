package com.reducerutils.tests.call.ForStmt;

public class ForStmt4 {
    
    public boolean func() {
        boolean _var_is_first_itr = false;
        for (int i = 0; i < 10;) {
            if (!_var_is_first_itr) {
                _var_is_first_itr = true;
            } else {
                int var_a = bar();
                i += var_a;
            }
            if (!(i < 10))
                break;
            boolean var_b = foo(100);
            assert (var_b);
        }
        return false;
    }

}
