import java.io.File;

public class WhileStmt2 {

    public boolean mut(File f, File b) {
        int a = 5;
        while (f.separator == "." && b.separator == "/") {
            a += f.hashCode();
        }
        
        return false;
    }
}
