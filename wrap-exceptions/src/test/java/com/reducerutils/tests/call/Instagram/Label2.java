package com.reducerutils.tests.call.Instagram;

import com.reducerutils.tests.npe.Instagram.JSONTokener;
import java.util.ArrayList;

public class Instagram2 {

    public static boolean func(String string) throws Exception {
        ArrayList<Object> jo = new ArrayList<Object>();
        JSONTokener x;
        try {
            x = new JSONTokener(string);
        } catch (java.lang.JavaLangException e) {
            return true;
        }
        if (x == null)
            return true;
        java.lang.Object var_b;
        try {
            var_b = x.nextTo('=');
        } catch (java.lang.RuntimeException e) {
            return true;
        }
        jo.add(var_b);
        x.next('=');
        java.lang.Object var_c = x.nextTo(';');
        jo.add(var_c);
        x.next();
        while (true) {
            boolean var_d = x.more();
            if (!(var_d)) {
                break;
            }
            Object value;
            java.lang.Object var_e;
            try {
                var_e = x.nextTo("=;");
            } catch (java.lang.NullPointerException e) {
                return true;
            }
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
                try {
                    value = var_h.toString();
                } catch (java.lang.ClassCastException e) {
                    return true;
                }
                x.next();
            }
            jo.add(value);
        }
        Object var_a = jo;
        return false;
    }
}