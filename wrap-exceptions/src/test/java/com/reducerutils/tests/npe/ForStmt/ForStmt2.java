package com.reducerutils.tests.npe.ForStmt;

import java.io.File;

public class ForStmt2 {
    public boolean mut(File foo) {
        for (int i = 0; i < 10; i++) {
            assert (foo.pathSeparatorChar == 'a');
        }

        return false;
    }
}
