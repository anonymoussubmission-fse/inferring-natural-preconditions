package com.reducerutils.tests.call.Ternary;

public class Ternary2 {

    public boolean func() {
        int var_a;
        try {
            var_a = bar();
        } catch (java.lang.ParseException e) {
            return true;
        }
        boolean var_b;
        try {
            var_b = foo(1);
        } catch (java.lang.ParseException e) {
            return true;
        }
        boolean var_c;
        try {
            var_c = foo(2);
        } catch (java.lang.RuntimeException e) {
            return true;
        } catch (java.lang.ParseException e) {
            return true;
        }
        boolean b = var_a > 15 ? var_b : var_c;
        return false;
    }
}
