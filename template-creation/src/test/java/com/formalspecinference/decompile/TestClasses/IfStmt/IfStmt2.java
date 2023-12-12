public class IfStmt2 {

	public boolean foo(int i) {
		return true;
	}

	public int bar() {
		return 1;
	}

	public void mut() {
		int i;
		if(foo(bar())) i = 0;
	}
}
