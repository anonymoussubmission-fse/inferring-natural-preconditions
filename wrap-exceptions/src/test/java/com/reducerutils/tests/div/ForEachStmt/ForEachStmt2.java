package com.reducerutils.tests.div.ForEachStmt;

import java.util.ArrayList;

public class ForEachStmt2 {
    public boolean mut(int foo) {
        ArrayList<Integer> l = new ArrayList<Integer>();
        l.add(1);
        l.add(2);
        l.add(3);

        int i = 0;
        for (Integer c : l) {
            i += c / foo * 2;
        }

        return false;
    }
}
