package com.reducerutils.tests.call.Assert;

public class Assert3 {
    public boolean func(String s) {
        int var_a = s.codePointAt(-1);

        try {
            boolean eq = s.toString() == "a";
        } catch (java.lang.NullPointerException e) {
            return true;
        }

        return false;
    }
}
