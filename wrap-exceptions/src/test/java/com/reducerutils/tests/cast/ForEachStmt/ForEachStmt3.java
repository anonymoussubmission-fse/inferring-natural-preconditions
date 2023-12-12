package com.reducerutils.tests.cast.ForEachStmt;

import java.util.ArrayList;

public class ForEachStmt3 {
        public boolean mut() {
        ArrayList<Integer> l = new ArrayList<Integer>();
        l.add(1);
        l.add(2);
        l.add(3);

        int i = 0;
        Object v;
        for (Integer c : l) v = (Object) c;

        return false;
    }
}
