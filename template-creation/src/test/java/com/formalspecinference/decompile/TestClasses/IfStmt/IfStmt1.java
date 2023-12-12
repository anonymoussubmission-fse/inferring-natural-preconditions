public class IfStmt1 {

	public boolean foo(int i) {
		return true;
	}

	public int bar() {
		return 1;
	}

	public void mut() {
		if (foo(bar())) {
			int i = 0;
		}
	}
}
