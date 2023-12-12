package com.reducerutils.tests.npe.ForStmt;

import java.util.Locale;
import java.util.TimeZone;

public class ForStmt4 {

    public boolean mut(TimeZone foo, Locale bar) {
        for (int i = 0; true; i++) {
            if (foo == null)
                return true;
            if (!(i < foo.LONG))
                break;
            if (bar == null)
                return true;
            if (bar == null)
                return true;
            assert (bar.KOREA == bar.FRENCH);
        }
        return false;
    }
}
