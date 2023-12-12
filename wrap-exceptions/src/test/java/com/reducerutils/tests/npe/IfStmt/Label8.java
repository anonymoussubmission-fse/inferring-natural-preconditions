public class IfStmt8 {

    public boolean mut(Boolean foo, Boolean bar, Double baz) {
        int a = 0;
        if (bar == null)
            return true;
        if (bar.TRUE) {
            if (foo == null)
                return true;
            if (foo.FALSE) {
                a += 1;
            } else {
                if (bar == null)
                    return true;
                a += bar.TRUE ? 1 : 0;
            }
        } else {
            if (baz == null)
                return true;
            if (baz.SIZE > 0) {
                a += 2;
            }
        }
        return false;
    }
}
