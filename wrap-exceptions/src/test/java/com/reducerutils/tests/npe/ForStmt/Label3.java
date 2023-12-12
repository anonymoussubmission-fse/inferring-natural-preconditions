package com.reducerutils.tests.npe.ForStmt;

import java.util.TimeZone;

public class ForStmt3 {

    public boolean mut(TimeZone foo, TimeZone bar) {
        for (int i = 0; true; i++) {
            if (foo == null)
                return true;
            if (!(i < foo.LONG))
                break;
            if (bar == null)
                return true;
            assert (bar.SHORT == 0);
        }
        return false;
    }
}
