package com.reducerutils.tests.aob.Assert;

public class Assert2 {
    public boolean mut(Double[] foo, Double[] bar) {
        assert(foo[bar.length] == 100 || bar[foo.length] == 100);
        return false;
    }
}