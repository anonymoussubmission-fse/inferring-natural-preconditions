public class ForEachStmt1 {

    public boolean mut(Object foo, Object bar) {
        String var_a = foo.toString();
        byte[] var_b = var_a.getBytes();
        for (Byte c : var_b) {
            System.out.println(c.SIZE);
       }

       return false;
    }
}
