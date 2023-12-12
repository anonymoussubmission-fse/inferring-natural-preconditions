import java.io.File;

public class IfStmt7 {

    public boolean mut(File foo, File bar, File baz) {
		if (bar.separatorChar == 0) {
			foo.canRead();
		} else if(foo.pathSeparatorChar == 0) {
			int a = 0;	
		} else if(baz.pathSeparator == "/") {
			foo.deleteOnExit();
		}

		return false;
	}
}
