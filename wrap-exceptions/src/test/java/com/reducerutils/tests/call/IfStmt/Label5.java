package com.reducerutils.tests.call.IfStmt;

public class IfStmt5 {

    public boolean func() {
        int var_a = bar();
        if (var_a == 0) {
            try {
                foo(1);
            } catch (java.lang.NullPointerException e) {
                return true;
            }
        } else {
            try {
                foo(0);
            } catch (java.lang.NullPointerException e) {
                return true;
            }
        }
        return false;
    }
}
