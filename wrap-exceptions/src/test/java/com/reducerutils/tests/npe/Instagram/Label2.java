package com.reducerutils.tests.npe.Instagram;

import java.util.ArrayList;

public class Instagram2 {

    public static boolean func(String string) throws Exception {
        ArrayList<Object> jo = new ArrayList<Object>();
        JSONTokener x = new JSONTokener(string);
        if (x == null)
            return true;
        java.lang.Object var_b = x.nextTo('=');
        jo.add(var_b);
        x.next('=');
        if (x == null)
            return true;
        java.lang.Object var_c = x.nextTo(';');
        jo.add(var_c);
        x.next();
        while (true) {
            if (x == null)
                return true;
            boolean var_d = x.more();
            if (!(var_d)) {
                break;
            }
            Object value;
            java.lang.Object var_e = x.nextTo("=;");
            if (var_e == null)
                return true;
            String name = var_e.toString();
            char var_f = x.next();
            if (var_f != '=') {
                boolean var_g = name.equals("secure");
                if (var_g) {
                    value = Boolean.TRUE;
                } else {
                    return true;
                }
            } else {
                java.lang.Object var_h = x.nextTo(';');
                value = var_h.toString();
                x.next();
            }
            if (jo == null)
                return true;
            jo.add(value);
        }
        Object var_a = jo;
        return false;
    }
}
