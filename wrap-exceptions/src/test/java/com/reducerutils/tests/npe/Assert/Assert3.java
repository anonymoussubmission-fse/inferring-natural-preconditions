package com.reducerutils.tests.npe.Assert;

public class Assert3 {
    public boolean mut(Double foo, Double bar) {
        String var_a = foo.toString();
        Integer var_b = var_a.codePointAt(0);
        assert(var_b.SIZE == 5 || bar.SIZE == 15);
        return false;
    }
}
