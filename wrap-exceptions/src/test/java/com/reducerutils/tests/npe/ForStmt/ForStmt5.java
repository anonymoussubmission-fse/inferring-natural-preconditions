package com.reducerutils.tests.npe.ForStmt;

import java.io.File;
import java.util.Locale;

public class ForStmt5 {
    public boolean mut(File foo, Locale bar) {
        for (int i=0; i<10; i+=foo.pathSeparatorChar) {
            assert(bar.ITALIAN.GERMAN.GERMANY == bar.CANADA_FRENCH);
        }

        return false;
    }
}
