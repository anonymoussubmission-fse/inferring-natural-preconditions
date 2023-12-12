package com.reducerutils.tests.npe.Assert;

public class Assert4 {
    public boolean mut(Double foo) {
        assert (Double.SIZE == 10 && foo.doubleValue() > 0);
        return false;
    }
}
