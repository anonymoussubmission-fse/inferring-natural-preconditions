package com.reducerutils.tests.div.ExprStmt;

public class ExprStmt4 {

    public boolean mut(Double foo, Integer bar) {
        if (true) {
            if (bar == 0)
                return true;
            Double var_a = foo / bar;
            Integer var_b = foo.intValue();
            if (var_b == 0)
                return true;
            String var_c = bar / var_b + 100 + "";
            char var_d = var_c.charAt(0);
        }
        return false;
    }
}
