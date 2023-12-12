package com.reducerutils.tests.cast.Assert;

public class Assert3 {

    public boolean mut(Object foo, Object bar) {
        String var_a = foo.toString();
        Integer var_b = var_a.codePointAt(0);
        if (!(foo instanceof Integer))
            return true;
        assert ((Integer) foo == 0);
        return false;
    }
}
