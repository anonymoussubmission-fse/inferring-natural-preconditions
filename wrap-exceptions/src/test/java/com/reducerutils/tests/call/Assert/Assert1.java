package com.reducerutils.tests.call.Assert;

public class Assert1 {
    public boolean func(String s) {
        int var_a = s.codePointAt(-1);
        boolean var_b = s.toString() == "a";
        assert (var_b);
        return false;
    }
}
