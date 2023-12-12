package com.formalspecinference.decompile.TestClasses.Instagram;

import com.formalspecinference.decompile.TestClasses.Instagram.Instagram2.JSONException;

public class Instagram3 {

    public static class JSONTokener {
        public JSONTokener(String string) {
        }

        public JSONException syntaxError(String message) {
            return new JSONException(message + toString());
        }

        public Object nextTo(char c) {
            return null;
        }

        public Object nextTo(String s) {
            return null;
        }

        public Object next(char c) {
            return null;
        }

        public char next() {
            return 'c';
        }

        public boolean more() {
            return false;
        }

        public static int dehexchar(char c) {
            return 0;
        }
    }

    public static String unescape(String string) {
        int length = string.length();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            char c = string.charAt(i);
            if (c == '+') {
                c = ' ';
            } else if (c == '%' && i + 2 < length) {
                int d = JSONTokener.dehexchar(string.charAt(i + 1));
                int e = JSONTokener.dehexchar(string.charAt(i + 2));
                if (d >= 0 && e >= 0) {
                    c = (char) (d * 16 + e);
                    i += 2;
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
