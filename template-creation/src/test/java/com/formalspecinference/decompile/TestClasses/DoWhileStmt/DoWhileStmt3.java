public class DoWhileStmt3 {

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
            a += 1;
        } while (foo(bar()));
    }
}
