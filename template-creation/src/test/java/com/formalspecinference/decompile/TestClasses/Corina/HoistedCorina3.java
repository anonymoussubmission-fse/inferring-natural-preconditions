public static boolean func(String spec, int width) {
    StringTokenizer tok = new StringTokenizer(spec, " *<>^|", true);
    int n = tok.countTokens();
    List<TabStop> tabs = new ArrayList();
    int mode = 0;
    float pos = 0.0F;
    for (int i = 0; i < n; i++) {
        String t = tok.nextToken();
        java.lang.String var_b = t.trim();
        int var_c = var_b.length();
        if (var_c != 0) {
            boolean var_d = t.equals("|");
            if (var_d) {
                mode = 5;
            } else {
                boolean var_e = t.equals(">");
                if (var_e) {
                    mode = 0;
                } else {
                    boolean var_f = t.equals("^");
                    if (var_f) {
                        mode = 2;
                    } else {
                        boolean var_g = t.equals("<");
                        if (var_g) {
                            mode = 1;
                        } else {
                            boolean var_h = t.equals("*");
                            if (var_h) {
                                mode = 4;
                            } else {
                                boolean var_i = t.endsWith("%");
                                if (var_i) {
                                    int var_j = t.length();
                                    java.lang.String var_k = t.substring(0, var_j - 1);
                                    Float value = new Float(var_k);
                                    pos += value.floatValue();
                                    int x = (int) (width * pos / 100.0F);
                                    javax.swing.text.TabStop var_l = new TabStop(x, mode, 0);
                                    tabs.add(var_l);
                                } else {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    int var_m = tabs.size();
    javax.swing.text.TabStop[] var_n = tabs.<TabStop>toArray(new TabStop[var_m]);
    TabSet var_a = new TabSet(var_n);
    return false;
}
