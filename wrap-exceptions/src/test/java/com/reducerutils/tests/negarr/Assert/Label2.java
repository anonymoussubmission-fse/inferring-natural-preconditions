package com.reducerutils.tests.negarr.Assert;

public class Assert2 {

    public boolean mut(Double foo, Double bar) {
        if (foo.SIZE < 0)
            return true;
        if (bar.SIZE < 0)
            return true;
        assert (new int[foo.SIZE] == null || new int[bar.SIZE] == null);
        return false;
    }
}
