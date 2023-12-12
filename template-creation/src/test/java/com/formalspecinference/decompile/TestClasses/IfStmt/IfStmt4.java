public class IfStmt4 {

	public boolean foo(int i) {
		return true;
	}

	public int bar() {
		return 1;
	}

	public void mut() {
		if (true) {
			int i = 0;
		} else if (foo(bar())) {
			int j = 0;
		} else {
			bar();
		}
	}
}
