package com.reducerutils.tests.aob.Assert;

public class Assert3 {
    public boolean mut(Double[] foo, Double[] bar) {
        String var_a = foo[0].toString();
        Integer var_b = var_a.codePointAt(0);
        assert(bar[var_b] == 5 || foo[var_b] == 15);
        return false;
    }
}
