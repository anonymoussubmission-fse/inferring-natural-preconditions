public class IfStmt9 {

	public boolean baz(int i) {
		return false;
	}


	public boolean foo(int i) {
		return true;
	}

	public int bar() {
		return 1;
	}

	public String hello() {
		return "bad";
	}

	public void mut() throws Exception {
		int a = 0;
		if (bar() == 0) {
			int b = 100;
			if (foo(1)) {
				a += 1;	
			} else {
				throw new Exception(hello());
			}

			assert(baz(100));

		} else {
			foo(a);
			if (baz(1)) {
				a += 2;
			}

			a -= 1;
		}
	}
}
