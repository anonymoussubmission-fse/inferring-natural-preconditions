public class Ternary1 {

	public boolean foo(int i) {
		return true;
	}

	public int bar() {
		return 1;
	}

	public void mut() {
		boolean b = foo(bar()) ? false : true;
	}
}
