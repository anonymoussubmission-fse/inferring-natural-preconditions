package com.reducerutils.tests.cast.Assert;

public class Assert1 {
    public boolean mut(Object foo) {
        assert (((Double) foo).SIZE == 10);
        return false;
    }
}
