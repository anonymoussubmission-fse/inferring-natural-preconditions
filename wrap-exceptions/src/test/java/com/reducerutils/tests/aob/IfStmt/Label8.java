package com.reducerutils.tests.aob.IfStmt;

public class IfStmt8 {

    public boolean mut(boolean[] foo, int bar, int baz) {
        int a = 0;
        if (bar < 0 || bar >= foo.length)
            return true;
        if (foo[bar]) {
            if (baz < 0 || baz >= foo.length)
                return true;
            if (foo[baz]) {
                a += 1;
            } else {
                if (bar + baz < 0 || bar + baz >= foo.length)
                    return true;
                a += foo[bar + baz] ? 1 : 0;
            }
        } else {
            if (bar < 0 || bar >= foo.length)
                return true;
            if (foo[bar] ? 1 : 0 < 0 || foo[bar] ? 1 : 0 >= foo.length)
                return true;
            if (foo[foo[bar] ? 1 : 0]) {
                a += 2;
            }
        }
        return false;
    }
}
