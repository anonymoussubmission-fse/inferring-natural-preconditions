package com.reducerutils.tests.aob.ForEachStmt;

public class ForEachStmt1 {

    public boolean mut(String[] foo, int[] bar) {
        if (0 < 0 || 0 >= bar.length)
            return true;
        if (bar[0] < 0 || bar[0] >= foo.length)
            return true;
        String var_a = foo[bar[0]];
        byte[] var_b = var_a.getBytes();
        for (Byte c : var_b) {
            if (foo.length - 1 < 0 || foo.length - 1 >= bar.length)
                return true;
            System.out.println(bar[foo.length - 1]);
        }
        return false;
    }
}
