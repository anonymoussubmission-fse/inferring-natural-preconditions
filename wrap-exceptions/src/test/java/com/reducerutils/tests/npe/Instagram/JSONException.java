package com.reducerutils.tests.npe.Instagram;

public class JSONException extends Exception {

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
