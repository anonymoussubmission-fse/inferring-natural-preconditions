import java.io.File;

public class IfStmt7 {

    public boolean mut(File foo, File bar, File baz) {
        if (bar == null)
            return true;
        if (bar.separatorChar == 0) {
            if (foo == null)
                return true;
            foo.canRead();
        } else {
            if (foo == null)
                return true;
            if (foo.pathSeparatorChar == 0) {
                int a = 0;
            } else {
                if (baz == null)
                    return true;
                if (baz.pathSeparator == "/") {
                    if (foo == null)
                        return true;
                    foo.deleteOnExit();
                }
            }
        }
        return false;
    }
}
