package com.reducerutils.tests.aob.IfStmt;

import java.io.File;

public class IfStmt7 {

    public boolean mut(File[] foo, int[] bar, int baz) {
        if (baz < 0 || baz >= bar.length)
            return true;
        if (bar[baz] < 0 || bar[baz] >= foo.length)
            return true;
        if (foo[bar[baz]].pathSeparator == ".") {
            foo[1].canRead();
        } else {
            if (baz < 0 || baz >= bar.length)
                return true;
            if (bar[baz] == 0) {
                int a = 0;
            } else {
                if (baz < 0 || baz >= foo.length)
                    return true;
                if (foo[baz].pathSeparator == "/") {
                    if (0 < 0 || 0 >= bar.length)
                        return true;
                    if (bar[0] < 0 || bar[0] >= foo.length)
                        return true;
                    foo[bar[0]].deleteOnExit();
                }
            }
        }
        return false;
    }
}
