package com.reducerutils.tests.npe.Instagram;

public class JSONTokener {
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
