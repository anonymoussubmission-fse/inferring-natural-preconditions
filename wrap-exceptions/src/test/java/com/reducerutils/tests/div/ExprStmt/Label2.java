public class ExprStmt2 {

    public boolean mut(int foo, int bar) {
        if (true) {
            if (bar == 0)
                return true;
            int a = foo / bar;
            if (foo == 0)
                return true;
            int b = bar / foo;
        }
        return false;
    }
}
