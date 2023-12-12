public class ForStmt3 {

    public boolean foo(int i) {
        return true;
    }

    public int bar() {
        return 1;
    }

    public void mut() {
        for (int i=0; i<bar(); i++) {
            assert(foo(i));
        }
    }
}
