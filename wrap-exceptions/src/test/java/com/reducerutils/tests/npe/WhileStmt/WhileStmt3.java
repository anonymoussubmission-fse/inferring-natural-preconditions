import java.io.File;

public class WhileStmt3 {

    public boolean mut(File f, File b, File c, File d) {
        int a = 5;
        while (f.separator == "." && b.separator == "/") {
            if (a > 10) {
                a += c.getTotalSpace();
            } else {
                a += d.length();
            }

            a += 15;	
        }
        return false;
    }
}
