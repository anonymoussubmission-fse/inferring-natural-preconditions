package com.reducerutils.tests.aob.ForStmt;

import java.util.TimeZone;

public class ForStmt2 {
    public boolean mut(int[] foo, int bar, TimeZone[] baz) {
        for (int i=0; i<foo[bar]; i++) {
            assert(baz[i] != null);
        }

        return false;
    }
}
