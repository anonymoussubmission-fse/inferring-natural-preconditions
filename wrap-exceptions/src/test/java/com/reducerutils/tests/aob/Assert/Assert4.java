package com.reducerutils.tests.aob.Assert;

public class Assert4 {
    public boolean mut(Double[] foo, int idx) {
        assert (foo[1] == 10 && foo[idx] == 10);
        return false;
    }
}
