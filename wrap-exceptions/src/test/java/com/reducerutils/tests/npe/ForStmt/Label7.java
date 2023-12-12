package com.reducerutils.tests.npe.ForStmt;

import java.io.File;
import java.util.Locale;

public class ForStmt7 {

    public boolean mut(File foo, Locale bar) {
        if (foo == null)
            return true;
        for (int i = foo.pathSeparatorChar; i < 10; i++) {
            if (bar == null)
                return true;
            if (bar.ITALIAN == null)
                return true;
            if (bar.ITALIAN.GERMAN == null)
                return true;
            if (bar == null)
                return true;
            assert (bar.ITALIAN.GERMAN.GERMANY == bar.CANADA_FRENCH);
        }
        return false;
    }
}
