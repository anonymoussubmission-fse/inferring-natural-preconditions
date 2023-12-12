package com.formalspecinference.decompile.TestClasses.Instagram;

import org.json.simple.JSONObject;

public class Instagram2 {

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
    }

    public static class JSONException extends Exception {

        protected static final long serialVersionUID = 0L;

        protected Throwable cause;

        public JSONException(String message) {
            super(message);
        }

        public JSONException(Throwable cause) {
            super(cause.getMessage());
            this.cause = cause;
        }

        public Throwable getCause() {
            return this.cause;
        }
    }

    public static String unescape(Object string) {
        return "hi";
    }

    public static JSONObject toJSONObject(String string) throws Exception {
        JSONObject jo = new JSONObject();
        JSONTokener x = new JSONTokener(string);
        jo.put("name", x.nextTo('='));
        x.next('=');
        jo.put("value", x.nextTo(';'));
        x.next();
        while (x.more()) {
            Object value;
            String name = unescape(x.nextTo("=;"));
            if (x.next() != '=') {
                if (name.equals("secure")) {
                    value = Boolean.TRUE;
                } else {
                    throw x.syntaxError("Missing '=' in cookie parameter.");
                }
            } else {
                value = unescape(x.nextTo(';'));
                x.next();
            }
            jo.put(name, value);
        }
        return jo;
    }
}
