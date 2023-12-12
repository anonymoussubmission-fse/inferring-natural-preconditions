package com.reducerutils.tests.div.Assert;

public class Assert4 {

    public boolean mut(Double[] foo, int idx) {
        if (idx < 0 || idx >= foo.length)
            return true;
        if (idx == 0)
            return true;
        assert (foo[idx] / idx == 0.0);
        return false;
    }
}
