package com.reducerutils.tests.npe.Assert;

public class Assert1 {

    public boolean mut(Double foo) {
        if (foo == null)
            return true;
        assert (foo.SIZE == 10);
        return false;
    }
}