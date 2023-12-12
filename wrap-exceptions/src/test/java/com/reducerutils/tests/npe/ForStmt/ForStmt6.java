import java.io.File;

public class ForStmt6 {
    public boolean mut(File f, File b) {
        for (int i = 0; i < 10; i += f.pathSeparatorChar) {
            if (!(f.pathSeparatorChar == b.pathSeparatorChar))
                return true;
        }

        return false;
    }
}
