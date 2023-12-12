public class ExprStmt1 {

    public boolean mut(int foo, int bar) {
        if (true) {
            if (bar == 0)
                return true;
            int a = foo / bar;
        }
        return false;
    }
}
