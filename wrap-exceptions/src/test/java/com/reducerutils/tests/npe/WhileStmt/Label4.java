import java.io.File;

public class WhileStmt4 {

    public boolean mut(File f, File b, File d) {
        int a = 5;
        while (true) {
            if (b == null)
                return true;
            if (f == null)
                return true;
            if (!(b.pathSeparator == "." && f.pathSeparator == "/")) {
                break;
            }
            if (b == null)
                return true;
            a += b.hashCode();
            while (true) {
                if (b == null)
                    return true;
                if (!(a < 42 + b.separatorChar)) {
                    break;
                }
                if (d == null)
                    return true;
                if (d.separatorChar == '.') {
                    continue;
                }
            }
        }
        return false;
    }
}
