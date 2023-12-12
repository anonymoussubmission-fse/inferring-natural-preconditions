package com.reducerutils.tests.call.Ternary;

public class Ternary1 {

    public boolean func() {
        int var_a;
        try {
            var_a = bar();
        } catch (java.lang.ParseException e) {
            return true;
        }
        boolean var_b;
        try {
            var_b = foo(var_a);
        } catch (java.lang.RuntimeException e) {
            return true;
        }
        boolean b = var_b ? false : true;
        return false;
    }
}
