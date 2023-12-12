public class ForEachStmt1 {

    public boolean mut(Object foo, Object bar) {
        if (foo == null)
            return true;
        String var_a = foo.toString();
        byte[] var_b = var_a.getBytes();
        for (Byte c : var_b) {
            if (System.out == null)
                return true;
            if (c == null)
                return true;
            System.out.println(c.SIZE);
        }
        return false;
    }
}
