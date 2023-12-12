public class ExprStmt2 {

    public boolean mut(Object foo, Object bar) {
        if (true) {
            if (foo == null)
                return true;
            foo.toString();
            if (bar == null)
                return true;
            bar.toString();
        }
        return false;
    }
}
