package com.reducerutils.tests.npe.Instagram;

public class Instagram3 {

    public static boolean func(String string) {
        int length = string.length();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (string == null)
                return true;
            char c = string.charAt(i);
            if (c == '+') {
                c = ' ';
            } else if (c == '%' && i + 2 < length) {
                if (string == null)
                    return true;
                char var_b = string.charAt(i + 1);
                int d = JSONTokener.dehexchar(var_b);
                if (string == null)
                    return true;
                char var_c = string.charAt(i + 2);
                int e = JSONTokener.dehexchar(var_c);
                if (d >= 0 && e >= 0) {
                    c = (char) (d * 16 + e);
                    i += 2;
                }
            }
            sb.append(c);
        }
        if (sb == null)
            return true;
        String var_a = sb.toString();
        return false;
    }
}
