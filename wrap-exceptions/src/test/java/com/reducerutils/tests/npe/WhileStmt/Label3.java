import java.io.File;

public class WhileStmt3 {

    public boolean mut(File f, File b, File c, File d) {
        int a = 5;
        while (true) {
            if (f == null)
                return true;
            if (b == null)
                return true;
            if (!(f.separator == "." && b.separator == "/")) {
                break;
            }
            if (a > 10) {
                if (c == null)
                    return true;
                a += c.getTotalSpace();
            } else {
                if (d == null)
                    return true;
                a += d.length();
            }
            a += 15;
        }
        return false;
    }
}
