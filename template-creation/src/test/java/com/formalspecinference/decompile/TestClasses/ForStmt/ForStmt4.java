public class ForStmt4 {

    public boolean foo(int i) {
        return true;
    }

    public int bar() {
        return 1;
    }

    public void mut() {
	for (int i=0; foo(bar()); i++) {
            assert(foo(100));
	}
    }
}
