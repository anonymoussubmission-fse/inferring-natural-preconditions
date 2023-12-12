package com.reducerutils.tests.div.Assert;

public class Assert1 {

    public boolean mut(Double[] bar, Integer foo) {
        if (foo == 0)
            return true;
        assert (bar[0] / foo == 1.0);
        return false;
    }
}