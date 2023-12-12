package com.reducerutils.tests.div.ForEachStmt;

public class ForEachStmt1 {

    public boolean mut(Double foo, Integer bar) {
        String var_a = foo.toString();
        byte[] var_b = var_a.getBytes();
        for (Byte c : var_b) {
            float var_c = c.floatValue();
            if (bar == 0)
                return true;
            var_c = var_c / bar;
        }
        return false;
    }
}
