package com.reducerutils.tests.call.Assert;

public class Assert1 {

    public boolean func(String s) {
        if (s == null)
            return true;
        int var_a;
        try {
            var_a = s.codePointAt(-1);
        } catch (java.lang.IndexOutOfBoundsException e) {
            return true;
        }
        boolean var_b = s.toString() == "a";
        assert (var_b);
        return false;
    }
}
