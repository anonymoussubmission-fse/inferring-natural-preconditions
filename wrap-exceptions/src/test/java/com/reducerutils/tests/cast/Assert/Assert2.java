package com.reducerutils.tests.cast.Assert;

public class Assert2 {
    public boolean mut(Object foo, Object bar) {
        assert((Double)foo == 100 || ((Double[])bar)[0] == 100);
        return false;
    }
}