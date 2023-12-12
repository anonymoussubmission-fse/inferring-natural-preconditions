package com.reducerutils.tests.cast.Assert;

public class Assert4 {
    public boolean mut(Object foo, int idx) {
        assert (((Double[]) foo)[idx] == 10);
        return false;
    }
}
