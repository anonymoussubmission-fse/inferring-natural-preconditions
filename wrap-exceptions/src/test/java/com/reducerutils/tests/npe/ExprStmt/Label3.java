public class ExprStmt3 {

    public boolean mut(Object foo, Object bar) {
        if (true) {
            if (foo == null)
                return true;
            String var_a = foo.toString();
            String var_b = bar.toString();
            if (var_a == null)
                return true;
            String var_c = var_a.concat(var_b);
            char var_d = var_c.charAt(0);
        }
        return false;
    }
}
