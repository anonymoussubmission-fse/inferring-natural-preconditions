package com.reducerutils.tests.div.Assert;

public class Assert2 {

    public boolean mut(Double[] foo, int bar) {
        if (bar < 0 || bar >= foo.length)
            return true;
        if (bar == 0)
            return true;
        assert (foo[bar] / bar == 0.0);
        return false;
    }
}
