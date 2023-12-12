package com.reducerutils.tests.cast.ForEachStmt;

import java.util.ArrayList;

public class ForEachStmt2 {
    public boolean mut() {
        ArrayList<Integer> l = new ArrayList<Integer>();
        l.add(1);
        l.add(2);
        l.add(3);

        int i = 0;
        for (Integer c : l) {
            Object v = (Object) c;
        }

        return false;
    }
}
