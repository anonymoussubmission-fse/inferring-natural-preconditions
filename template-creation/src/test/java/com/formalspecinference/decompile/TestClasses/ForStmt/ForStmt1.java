public class ForStmt1 {

    public boolean foo(int i) {
        return true;
    }

    public int bar() {
        return 1;
    }

    public void mut() {
	for (int i=0; i<10; i++) {
            foo(bar());
	}
    }
}
