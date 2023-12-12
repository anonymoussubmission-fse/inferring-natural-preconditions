package com.reducerutils;

enum WrapType {
    CALL, NPE, CAST, NEGARRAY, DIVZERO, AOB
}

public class Wrap {
    private int lineNo;
    private String[] exceptionType;
    private WrapType wrapType;

    public Wrap(int lineNo, String exceptionType, WrapType wrapType) {
        this.lineNo = lineNo;
        this.exceptionType = new String[] { exceptionType };
        this.wrapType = wrapType;
    }

    public Wrap(int lineNo, String[] exceptionType, WrapType wrapType) {
        this.lineNo = lineNo;
        this.exceptionType = exceptionType;
        this.wrapType = wrapType;
    }

    public int getLineNo() {
        return lineNo;
    }

    public WrapType getWrapType() {
        return wrapType;
    }

    public String[] getExceptionTypes() {
        return exceptionType;
    }

    public String getExceptionType() {
        assert (exceptionType.length == 1);
        return exceptionType[0];
    }
}
