public class ExprStmt1 {

    public boolean mut(Object foo) {
        if (true) {
            if (foo == null)
                return true;
            foo.toString();
        }
        return false;
    }
}
