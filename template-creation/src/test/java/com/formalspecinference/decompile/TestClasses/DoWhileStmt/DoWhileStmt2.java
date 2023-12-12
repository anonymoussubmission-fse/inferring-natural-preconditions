import java.io.*;

public class DoWhileStmt2 {

    protected char[] cbuf;

    protected boolean stop;

    private int getLimit() {
        return 16386;
    }

    public void copy(File from, File to) throws IOException {
        InputStreamReader  r = new InputStreamReader(new FileInputStream(from));
        OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(to));
	int n;
	do {
		n = r.read(this.cbuf, 0, getLimit());
		if (this.stop)
			return;
		if (n <= 0)
			continue;
                w.write(this.cbuf, 0, n);
	} while (n == getLimit());
        long mod = from.lastModified();
        to.setLastModified(mod);
    }
}
