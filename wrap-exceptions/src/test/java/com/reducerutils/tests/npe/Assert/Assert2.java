package com.reducerutils.tests.npe.Assert;

public class Assert2 {
    public boolean mut(Double foo, Double bar) {
        assert(foo.SIZE == 100 || bar.SIZE == 100);
        return false;
    }
}