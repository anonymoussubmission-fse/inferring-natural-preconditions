package com.reducerutils.tests.div.Assert;

public class Assert2 {
    public boolean mut(Double[] foo, int bar) {
        assert(foo[bar] / bar == 0.0);
        return false;
    }
}