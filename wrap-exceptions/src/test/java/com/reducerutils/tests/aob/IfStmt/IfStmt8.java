package com.reducerutils.tests.aob.IfStmt;

public class IfStmt8 {

	public boolean mut(boolean[] foo, int bar, int baz) {
		int a = 0;
		if (foo[bar]) {
			if (foo[baz]) {
				a += 1;	
			} else {
				a += foo[bar + baz] ? 1 : 0;
			}

		} else {
			if (foo[foo[bar] ? 1 : 0]) {
				a += 2;
			}
		}

		return false;
	}
}
