package com.reducerutils.tests.div.ExprStmt;

public class ExprStmt3 {

    public boolean mut(Double foo, Integer bar) {
        if (true) {
            Double var_a = foo / bar;
            Integer var_b = foo.intValue();
            String var_c = bar / var_b + "";
            char var_d = var_c.charAt(0);
        }

        return false;
    }
}
