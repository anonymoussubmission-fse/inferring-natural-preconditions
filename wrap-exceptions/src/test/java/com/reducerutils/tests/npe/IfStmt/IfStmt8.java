public class IfStmt8 {

	public boolean mut(Boolean foo, Boolean bar, Double baz) {
		int a = 0;
		if (bar.TRUE) {
			if (foo.FALSE) {
				a += 1;	
			} else {
				a += bar.TRUE ? 1 : 0;
			}

		} else {
			if (baz.SIZE > 0) {
				a += 2;
			}
		}

		return false;
	}
}
