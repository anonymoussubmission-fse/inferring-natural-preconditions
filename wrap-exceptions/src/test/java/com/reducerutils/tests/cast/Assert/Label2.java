package com.reducerutils.tests.cast.Assert;

public class Assert2 {

    public boolean mut(Object foo, Object bar) {
        if (!(foo instanceof Double))
            return true;
        if (!(bar instanceof Double[]))
            return true;
        if (0 < 0 || 0 >= ((Double[]) bar).length)
            return true;
        assert ((Double) foo == 100 || ((Double[]) bar)[0] == 100);
        return false;
    }
}
