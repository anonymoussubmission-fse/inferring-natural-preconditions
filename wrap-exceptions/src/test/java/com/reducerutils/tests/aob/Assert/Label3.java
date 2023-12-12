package com.reducerutils.tests.aob.Assert;

public class Assert3 {

    public boolean mut(Double[] foo, Double[] bar) {
        if (0 < 0 || 0 >= foo.length)
            return true;
        String var_a = foo[0].toString();
        Integer var_b = var_a.codePointAt(0);
        if (var_b < 0 || var_b >= bar.length)
            return true;
        if (var_b < 0 || var_b >= foo.length)
            return true;
        assert (bar[var_b] == 5 || foo[var_b] == 15);
        return false;
    }
}
