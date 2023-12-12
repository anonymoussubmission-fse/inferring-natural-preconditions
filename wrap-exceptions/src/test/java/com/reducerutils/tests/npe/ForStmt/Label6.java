import java.io.File;

public class ForStmt6 {

    public boolean mut(File f, File b) {
        boolean _var_is_first_itr = false;
        for (int i = 0; i < 10; ) {
            if (!_var_is_first_itr) {
                _var_is_first_itr = true;
            } else {
                if (f == null)
                    return true;
                i += f.pathSeparatorChar;
            }
            if (!(i < 10))
                break;
            if (f == null)
                return true;
            if (b == null)
                return true;
            if (!(f.pathSeparatorChar == b.pathSeparatorChar)) {
                return true;
            }
        }
        return false;
    }
}
