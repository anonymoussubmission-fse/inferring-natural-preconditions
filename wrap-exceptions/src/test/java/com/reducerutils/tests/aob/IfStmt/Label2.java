package com.reducerutils.tests.aob.IfStmt;

public class IfStmt2 {

    public boolean mut(String[] s, int foo) {
        int i;
        if (foo < 0 || foo >= s.length)
            return true;
        if (s[foo] != null) {
            i = 0;
        }
        return false;
    }
}
