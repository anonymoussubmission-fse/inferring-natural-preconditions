package com.reducerutils.tests.div.Assert;

public class Assert4 {
    public boolean mut(Double[] foo, int idx) {
        assert (foo[idx] / idx == 0.0);
        return false;
    }
}
