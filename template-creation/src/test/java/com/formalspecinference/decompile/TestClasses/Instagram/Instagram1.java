package com.formalspecinference.decompile.TestClasses.Instagram;

public class Instagram1 {
    public static String escape(String string) {
        String s = string.trim();
        StringBuffer sb = new StringBuffer();
        int length = s.length();
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            if (c < ' ' || c == '+' || c == '%' || c == '=' || c == ';') {
                sb.append('%');
                sb.append(Character.forDigit((char) (c >>> 4 & 0xF), 16));
                sb.append(Character.forDigit((char) (c & 0xF), 16));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
