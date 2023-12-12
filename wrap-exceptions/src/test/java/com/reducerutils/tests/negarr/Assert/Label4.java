package com.reducerutils.tests.negarr.Assert;

public class Assert4 {

    public boolean mut(int foo, int bar) {
        if (foo < 0)
            return true;
        if (bar < 0)
            return true;
        assert (new Object[foo][bar] == null);
        return false;
    }
}