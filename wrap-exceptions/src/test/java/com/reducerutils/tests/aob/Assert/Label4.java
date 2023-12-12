package com.reducerutils.tests.aob.Assert;

public class Assert4 {

    public boolean mut(Double[] foo, int idx) {
        if (1 < 0 || 1 >= foo.length)
            return true;
        if (idx < 0 || idx >= foo.length)
            return true;
        assert (foo[1] == 10 && foo[idx] == 10);
        return false;
    }
}
