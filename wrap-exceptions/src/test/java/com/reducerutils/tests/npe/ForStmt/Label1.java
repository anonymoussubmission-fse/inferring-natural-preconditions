public class ForStmt1 {

    public boolean mut(Object foo) {
        for (int i = 0; i < 10; i++) {
            if (foo == null)
                return true;
            String var_a = foo.toString();
            char var_b = var_a.charAt(i);
        }
        return false;
    }
}
