public static boolean func(String string) throws Exception {
    JSONObject jo = new JSONObject();
    JSONTokener x = new JSONTokener(string);
    java.lang.Object var_b = x.nextTo('=');
    jo.put("name", var_b);
    x.next('=');
    java.lang.Object var_c = x.nextTo(';');
    jo.put("value", var_c);
    x.next();
    while (true) {
        boolean var_d = x.more();
        if (!(var_d)) {
            break;
        }
        Object value;
        java.lang.Object var_e = x.nextTo("=;");
        String name = unescape(var_e);
        char var_f = x.next();
        if (var_f != '=') {
            boolean var_g = name.equals("secure");
            if (var_g) {
                value = Boolean.TRUE;
            } else {
                return true;
            }
        } else {
            java.lang.Object var_h = x.nextTo(';');
            value = unescape(var_h);
            x.next();
        }
        jo.put(name, value);
    }
    JSONObject var_a = jo;
    return false;
}
