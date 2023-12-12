import java.io.File;

public class WhileStmt2 {

    public boolean mut(File f, File b) {
        int a = 5;
        while (true) {
            if (f == null)
                return true;
            if (b == null)
                return true;
            if (!(f.separator == "." && b.separator == "/")) {
                break;
            }
            if (f == null)
                return true;
            a += f.hashCode();
        }
        return false;
    }
}
