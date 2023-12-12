public class IfStmt6 {

	public boolean foo(int i) {
		return true;
	}

	public int bar() {
		return 1;
	}

	public void mut() {
		if (bar() == 0) foo(1);
		else foo(0);
	}
}
