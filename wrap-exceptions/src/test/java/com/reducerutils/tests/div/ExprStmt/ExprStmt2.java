public class ExprStmt2 {

    public boolean mut(int foo, int bar) {
        if (true) {
            int a = foo / bar;
            int b = bar / foo;
        }

        return false;
    }
}
