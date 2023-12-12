package com.reducerutils.tests.npe.ForStmt;

import java.util.TimeZone;

public class ForStmt3 {
    public boolean mut(TimeZone foo, TimeZone bar) {
        for (int i=0; i<foo.LONG; i++) {
            assert(bar.SHORT == 0);
        }

        return false;
    }
}
