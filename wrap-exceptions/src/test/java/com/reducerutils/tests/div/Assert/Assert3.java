package com.reducerutils.tests.div.Assert;

public class Assert3 {
    public boolean mut(Integer foo, Integer bar) {
        String var_a = foo.toString();
        Integer var_b = var_a.codePointAt(0);
        assert(foo / bar == 0);
        return false;
    }
}
