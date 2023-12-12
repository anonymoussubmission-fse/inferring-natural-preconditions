public boolean func(File from, File to) {
    java.io.FileInputStream var_a = new FileInputStream(from);
    InputStreamReader r = new InputStreamReader(var_a);
    java.io.FileOutputStream var_b = new FileOutputStream(to);
    OutputStreamWriter w = new OutputStreamWriter(var_b);
    int n;
    do {
        int var_c = getLimit();
        n = r.read(this.cbuf, 0, var_c);
        if (this.stop)
            return false;
        if (n <= 0)
            continue;
        w.write(this.cbuf, 0, n);
        int var_d = getLimit();
    } while (n == var_d);
    long mod = from.lastModified();
    to.setLastModified(mod);
    return false;
}
