package com.reducerutils.tests.call.Instagram;

public class Instagram4 {

    public boolean func(Object o) {
        if (o == null) {
            boolean var_a = false;
            return false;
        }
        if (o == this) {
            boolean var_b = true;
            return false;
        }
        if (o == null)
            return true;
        java.lang.Class<? extends java.lang.Object> var_e;
        try {
            var_e = o.getClass();
        } catch (java.lang.NullPointerException e) {
            return true;
        }
        java.lang.Class<? extends java.lang.Object> var_f = getClass();
        if (var_e != var_f) {
            boolean var_c = false;
            return false;
        }
        if (!(o instanceof Object))
            return true;
        java.lang.String var_g;
        try {
            var_g = ((Object) o).toString();
        } catch (java.lang.NullPointerException e) {
            return true;
        }
        java.lang.String var_h;
        try {
            var_h = o.toString();
        } catch (java.lang.NullPointerException e) {
            return true;
        }
        boolean var_d = var_g.equals(var_h);
        return false;
    }
}