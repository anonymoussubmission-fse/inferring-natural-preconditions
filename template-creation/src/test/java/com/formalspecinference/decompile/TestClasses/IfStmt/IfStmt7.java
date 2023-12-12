public class IfStmt7 {

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
		if (bar() == 0) {
			foo(1);
		} else if(foo(0)) {
			int a = 0;	
		} else if(baz(1)) {
			foo(7);
		}
	}
}
