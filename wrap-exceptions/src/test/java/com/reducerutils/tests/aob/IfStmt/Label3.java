package com.reducerutils.tests.aob.IfStmt;

public class IfStmt3 {

    public boolean mut(Integer[] b) {
        if (true) {
            int i = 0;
        } else {
            if (100 < 0 || 100 >= b.length)
                return true;
            if (b[100] > 0) {
                int j = 0;
            }
        }
        return false;
    }
}
