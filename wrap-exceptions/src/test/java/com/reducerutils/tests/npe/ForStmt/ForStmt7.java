package com.reducerutils.tests.npe.ForStmt;

import java.io.File;
import java.util.Locale;

public class ForStmt7 {

    public boolean mut(File foo, Locale bar) {
        for (int i=foo.pathSeparatorChar; i<10; i++) {
            assert(bar.ITALIAN.GERMAN.GERMANY == bar.CANADA_FRENCH);
        }

        return false;
    }
}
