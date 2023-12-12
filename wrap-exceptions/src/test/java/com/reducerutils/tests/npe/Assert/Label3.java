package com.reducerutils.tests.npe.Assert;

public class Assert3 {

    public boolean mut(Double foo, Double bar) {
        if (foo == null)
            return true;
        String var_a = foo.toString();
        if (var_a == null)
            return true;
        Integer var_b = var_a.codePointAt(0);
        if (var_b == null)
            return true;
        if (bar == null)
            return true;
        assert (var_b.SIZE == 5 || bar.SIZE == 15);
        return false;
    }
}
