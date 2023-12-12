package com.reducerutils.tests.aob.ForStmt;

import java.util.TimeZone;

public class ForStmt2 {

    public boolean mut(int[] foo, int bar, TimeZone[] baz) {
        for (int i = 0; true; i++) {
            if (bar < 0 || bar >= foo.length)
                return true;
            if (!(i < foo[bar]))
                break;
            if (i < 0 || i >= baz.length)
                return true;
            assert (baz[i] != null);
        }
        return false;
    }
}
