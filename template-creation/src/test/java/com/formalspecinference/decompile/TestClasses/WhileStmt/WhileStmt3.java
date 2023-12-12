public class WhileStmt3 {

	public boolean foo(int i) {
		return true;
	}

	public int bar() {
		return 1;
	}

	public int baz() {
		return 1;
	}

	public int eliz() {
		return 1;
	}



	public int mut() {
		int a = 5;
		while (foo(bar())) {
			if (a > 10) {
				a += baz();
			} else {
				a += eliz();
			}

			a += 15;	
		}

		return a;
	}
}
