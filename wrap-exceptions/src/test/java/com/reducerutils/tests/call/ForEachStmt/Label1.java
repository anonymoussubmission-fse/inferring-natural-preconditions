package com.reducerutils.tests.call.ForEachStmt;

public class ForEachStmt1 {

    public boolean func() {
        java.util.Collection<java.lang.Integer> var_a;
        try {
            var_a = foo();
        } catch (java.lang.NullPointerException e) {
            return true;
        }
        for (Integer c : var_a) {
            System.out.println(c);
        }
        return false;
    }
}
