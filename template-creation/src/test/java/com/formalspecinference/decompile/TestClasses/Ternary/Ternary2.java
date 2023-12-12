public class Ternary2 {

	public boolean foo(int i) {
		return true;
	}

	public int bar() {
		return 1;
	}

	public void mut() {
		boolean b = bar() > 15 ? foo(1) : foo(2);
	}
}
