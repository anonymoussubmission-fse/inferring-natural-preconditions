public class IfStmt5 {

	public boolean baz(int i) {
		return false;
	}


	public boolean foo(int i) {
		return true;
	}

	public int bar() {
		return 1;
	}

	public void mut() throws Exception {
		if (bar() == 0) foo(1);

		if (bar() == 100) throw new Exception();
	}
}
