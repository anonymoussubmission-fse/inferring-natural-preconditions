package com.reducerutils.tests.aob.IfStmt;

public class IfStmt5 {

    public boolean mut(Integer[] i, int o) throws Exception {
        if (o < 0 || o >= i.length)
            return true;
        if (i[o] == 0) {
            i.toString();
        }
        if (1 < 0 || 1 >= i.length)
            return true;
        if (o + i[1] < 0 || o + i[1] >= i.length)
            return true;
        if (i[o + i[1]] == null) {
            return true;
        }
        return false;
    }
}
