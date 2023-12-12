protected boolean func() {
    this.childrenDefined = true;
    java.io.File var_a = new File(this.folder);
    File[] files = (var_a).listFiles();
    if (files == null)
        return false;
    List<String> buf = new ArrayList();
    int i;
    for (i = 0; i < files.length; i++) {
        boolean var_b = files[i].isDirectory();
        boolean var_c = files[i].isHidden();
        if (var_b && !var_c) {
            java.lang.String var_d = files[i].getPath();
            buf.add(var_d);
        }
    }
    NaturalSort var_e = new NaturalSort();
    Collections.sort(buf, var_e);
    for (i = 0; true; i++) {
        int var_f = buf.size();
        if (!(i < var_f))
            break;
        java.lang.String var_g = buf.get(i);
        FolderNode var_h = new FolderNode(var_g);
        add(var_h);
    }
    return false;
}

