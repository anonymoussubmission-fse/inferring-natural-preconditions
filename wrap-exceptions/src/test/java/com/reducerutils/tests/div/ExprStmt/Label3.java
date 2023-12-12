package com.reducerutils.tests.div.ExprStmt;

public class ExprStmt3 {

    public boolean mut(Double foo, Integer bar) {
        if (true) {
            if (bar == 0)
                return true;
            Double var_a = foo / bar;
            Integer var_b = foo.intValue();
            if (var_b == 0)
                return true;
            String var_c = bar / var_b + "";
            char var_d = var_c.charAt(0);
        }
        return false;
    }
}
