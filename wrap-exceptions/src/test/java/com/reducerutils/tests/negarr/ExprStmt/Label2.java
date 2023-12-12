public class ExprStmt2 {

    public boolean mut(int foo, int bar) {
        if (true) {
            if (foo < 0)
                return true;
            int[] var_a = new int[foo];
            if (var_a == null)
                return true;
            if (foo / bar + var_a.length < 0) {
                return true;
            }
            int[] var_b = new int[foo / bar + var_a.length];
        }
        return false;
    }
}
