public class DoWhileStmt4 {

    public boolean foo(int i) {
        return true;
    }

    public int bar() {
        return 1;
    }

    public void mut() {
        int a = 0;
        int b = 1;
        do {
            foo(bar());
        } while (a < b);
    }
}
