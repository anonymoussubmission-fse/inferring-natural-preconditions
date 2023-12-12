public class IfStmt9 {

    public boolean mut(Boolean foo, Boolean bar, Boolean baz) throws Exception {
        int a = 0;
        if (bar == null)
            return true;
        if (bar.TRUE) {
            int b = 100;
            if (foo == null)
                return true;
            if (foo.FALSE) {
                a += 1;
            } else {
                return true;
            }
            if (baz == null)
                return true;
            assert (baz.TRUE == true);
        } else {
            if (foo == null)
                return true;
            foo.toString();
            if (baz == null)
                return true;
            if (baz.FALSE == false) {
                a += 2;
            }
            a -= 1;
        }
        return false;
    }
}
