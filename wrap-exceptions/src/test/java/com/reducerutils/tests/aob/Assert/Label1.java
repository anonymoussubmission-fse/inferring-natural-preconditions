package com.reducerutils.tests.aob.Assert;

public class Assert1 {

    public boolean mut(Double[] foo) {
        if (1 < 0 || 1 >= foo.length)
            return true;
        assert (foo[1] == 10);
        return false;
    }
}