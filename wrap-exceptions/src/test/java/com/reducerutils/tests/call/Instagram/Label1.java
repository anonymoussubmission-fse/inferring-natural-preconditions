package com.reducerutils.tests.call.Instagram;

public class Instagram1 {

    public static boolean func(String string) {
        String s = string.trim();
        StringBuffer sb;
        try {
            sb = new StringBuffer();
        } catch (java.lang.NullPointerException e) {
            return true;
        }
        int length;
        try {
            length = s.length();
        } catch (java.lang.NullPointerException e) {
            return true;
        }
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            if (c < ' ' || c == '+' || c == '%' || c == '=' || c == ';') {
                sb.append('%');
                char var_b;
                try {
                    var_b = Character.forDigit((char) (c >>> 4 & 0xF), 16);
                } catch (java.lang.NullPointerException e) {
                    return true;
                }
                sb.append(var_b);
                char var_c = Character.forDigit((char) (c & 0xF), 16);
                sb.append(var_c);
            } else {
                try {
                    sb.append(c);
                } catch (java.lang.NullPointerException e) {
                    return true;
                }
            }
        }
        String var_a = sb.toString();
        return false;
    }
}