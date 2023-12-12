public class IfStmt8 {

	public boolean baz(int i) {
		return false;
	}


	public boolean foo(int i) {
		return true;
	}

	public int bar() {
		return 1;
	}

	public void mut() {
		int a = 0;
		if (bar() == 0) {
			if (foo(1)) {
				a += 1;	
			} else {
				a += bar();
			}

		} else {
			if (baz(1)) {
				a += 2;
			}
		}
	}
}
