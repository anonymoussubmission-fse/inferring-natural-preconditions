package com.reducerutils.tests.aob.Assert;

public class Assert2 {

    public boolean mut(Double[] foo, Double[] bar) {
        if (bar.length < 0 || bar.length >= foo.length)
            return true;
        if (foo.length < 0 || foo.length >= bar.length)
            return true;
        assert (foo[bar.length] == 100 || bar[foo.length] == 100);
        return false;
    }
}
