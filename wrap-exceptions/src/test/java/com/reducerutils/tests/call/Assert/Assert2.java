package com.reducerutils.tests.call.Assert;

public class Assert2 {
    public boolean func(String s) {
        try {
            int var_a = s.codePointAt(-1);
            boolean var_b = s.toString() == "a";
            assert (var_b);
        } catch (java.lang.NullPointerException e) {
            return true;
        }

        return false;
    }
}
