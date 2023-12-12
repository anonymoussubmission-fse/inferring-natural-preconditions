package com.reducerutils.tests.negarr.Assert;

public class Assert1 {

    public boolean mut(int foo) {
        if (foo < 0)
            return true;
        assert (new int[foo] == null);
        return false;
    }
}