package com.reducerutils.tests.npe.Instagram;

public class Instagram1 {

    public static boolean func(String string) {
        if (string == null)
            return true;
        String s = string.trim();
        StringBuffer sb = new StringBuffer();
        if (s == null)
            return true;
        int length = s.length();
        for (int i = 0; i < length; i++) {
            if (s == null)
                return true;
            char c = s.charAt(i);
            if (c < ' ' || c == '+' || c == '%' || c == '=' || c == ';') {
                if (sb == null)
                    return true;
                sb.append('%');
                char var_b = Character.forDigit((char) (c >>> 4 & 0xF), 16);
                sb.append(var_b);
                char var_c = Character.forDigit((char) (c & 0xF), 16);
                sb.append(var_c);
            } else {
                if (sb == null)
                    return true;
                sb.append(c);
            }
        }
        String var_a = sb.toString();
        return false;
    }
}
