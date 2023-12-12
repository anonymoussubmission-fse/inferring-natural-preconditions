package com.reducerutils.tests.negarr.Assert;

public class Assert3 {

    public boolean mut(Double foo, int bar) {
        String var_a = foo.toString();
        if (foo.SIZE < 0)
            return true;
        Integer[] var_b = new Integer[foo.SIZE];
        if (bar < 0)
            return true;
        assert (new int[bar] == null);
        return false;
    }
}
