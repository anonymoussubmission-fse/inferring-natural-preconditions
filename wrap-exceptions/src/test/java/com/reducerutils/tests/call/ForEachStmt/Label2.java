package com.reducerutils.tests.call.ForEachStmt;

import java.util.ArrayList;

public class ForEachStmt2 {

    public boolean func() {
        ArrayList<Integer> l = new ArrayList<Integer>();
        try {
            l.add(1);
        } catch (java.lang.RuntimeException e) {
            return true;
        }
        if (l == null)
            return true;
        l.add(2);
        l.add(3);
        int i = 0;
        for (Integer c : l) {
            int var_a;
            try {
                var_a = foo();
            } catch (java.lang.RuntimeException e) {
                return true;
            }
            int var_b = foo();
            i = var_a + var_b;
        }
        return false;
    }
}
