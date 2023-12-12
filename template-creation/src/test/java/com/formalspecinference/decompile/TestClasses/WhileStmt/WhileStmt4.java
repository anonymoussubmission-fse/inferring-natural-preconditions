public class WhileStmt4 {

	public boolean foo(int i) {
		return true;
	}

	public int bar() {
		return 1;
	}

	public int baz() {
		return 1;
	}


	public int mut() {
		int a = 5;
		while (foo(bar())) {
			a += baz();	
			while (a < 42 + bar()) {
				if (foo(100)) {
					continue;
				}
			}
		}
		return a;
	}
}
