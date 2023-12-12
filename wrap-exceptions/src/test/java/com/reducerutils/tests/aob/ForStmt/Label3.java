package com.reducerutils.tests.aob.ForStmt;

public class ForStmt3 {

    public boolean mut(int[] foo, int bar) {
        boolean _var_is_first_itr = false;
        for (int i = 0; i < 10; ) {
            if (!_var_is_first_itr) {
                _var_is_first_itr = true;
            } else {
                if (bar < 0 || bar >= foo.length)
                    return true;
                i += foo[bar];
            }
            if (!(i < 10))
                break;
            if (bar + i < 0 || bar + i >= foo.length)
                return true;
            assert (bar == foo[bar + i]);
        }
        return false;
    }
}
