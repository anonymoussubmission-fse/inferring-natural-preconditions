package com.reducerutils.tests.cast.Assert;

public class Assert4 {

    public boolean mut(Object foo, int idx) {
        if (!(foo instanceof Double[]))
            return true;
        if (idx < 0 || idx >= ((Double[]) foo).length)
            return true;
        assert (((Double[]) foo)[idx] == 10);
        return false;
    }
}
