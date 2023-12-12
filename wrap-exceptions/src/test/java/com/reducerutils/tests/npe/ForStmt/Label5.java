package com.reducerutils.tests.npe.ForStmt;

import java.io.File;
import java.util.Locale;

public class ForStmt5 {

    public boolean mut(File foo, Locale bar) {
        boolean _var_is_first_itr = false;
        for (int i = 0; i < 10; ) {
            if (!_var_is_first_itr) {
                _var_is_first_itr = true;
            } else {
                if (foo == null)
                    return true;
                i += foo.pathSeparatorChar;
            }
            if (!(i < 10))
                break;
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
