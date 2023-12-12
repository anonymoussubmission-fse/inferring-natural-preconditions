public class ExprStmt2 {

    public boolean mut(int foo, int bar) {
        if (true) {
            int[] var_a = new int[foo];
            int[] var_b = new int[foo / bar + var_a.length];
        }

        return false;
    }
}
