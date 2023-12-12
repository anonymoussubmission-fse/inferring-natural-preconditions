package com.reducerutils.tests.aob.ForEachStmt;

public class ForEachStmt1 {

    public boolean mut(String[] foo, int[] bar) {
        String var_a = foo[bar[0]];
        byte[] var_b = var_a.getBytes();
        for (Byte c : var_b) {
            System.out.println(bar[foo.length - 1]);
       }

       return false;
    }
}
