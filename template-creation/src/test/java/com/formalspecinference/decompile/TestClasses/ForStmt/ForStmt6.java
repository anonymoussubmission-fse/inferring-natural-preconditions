public class ForStmt6 {

    public boolean foo(int i) {
        return true;
    }

    public int bar() {
        return 1;
    }

    public void mut() {
	for (int i=0; i<10; i+=bar()) {
	    if (!foo(i)) throw new RuntimeException();
            
	}
    }
}
