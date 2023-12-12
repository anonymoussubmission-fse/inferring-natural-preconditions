package com.reducerutils.tests.aob.IfStmt;

public class IfStmt9 {

	public boolean mut(boolean[] foo, int bar, int baz) throws Exception {
		int a = 0;
		if (foo[bar]) {
			int b = 100;
			if (foo[baz]) {
				a += 1;	
			} else {
				return true;
			}

			assert((foo[baz] ? 1 : 0) > 0);

		} else {
			foo.toString();
			if (foo[baz] == false) {
				a += 2;
			}

			a -= 1;
		}
		
		return false;

	}

}
