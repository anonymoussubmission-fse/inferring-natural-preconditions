package com.reducerutils.tests.npe.Assert;

public class Assert2 {

    public boolean mut(Double foo, Double bar) {
        if (foo == null)
            return true;
        if (bar == null)
            return true;
        assert (foo.SIZE == 100 || bar.SIZE == 100);
        return false;
    }
}
