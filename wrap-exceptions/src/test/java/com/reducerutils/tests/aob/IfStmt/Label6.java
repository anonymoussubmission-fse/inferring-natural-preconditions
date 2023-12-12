package com.reducerutils.tests.aob.IfStmt;

import java.io.File;

public class IfStmt6 {

    public boolean mut(File[] foo, int bar) {
        if (bar < 0 || bar >= foo.length)
            return true;
        if (bar + 1 < 0 || bar + 1 >= foo.length)
            return true;
        if (foo[bar].pathSeparator == "." && foo[bar + 1].pathSeparator == "/") {
            foo.toString();
        } else {
            foo.hashCode();
        }
        return false;
    }
}
