package com.reducerutils.tests.npe.ForStmt;

import java.util.Locale;
import java.util.TimeZone;

public class ForStmt4 {
    public boolean mut(TimeZone foo, Locale bar) {
        for (int i = 0; i < foo.LONG; i++) {
            assert (bar.KOREA == bar.FRENCH);
        }

        return false;
    }
}
