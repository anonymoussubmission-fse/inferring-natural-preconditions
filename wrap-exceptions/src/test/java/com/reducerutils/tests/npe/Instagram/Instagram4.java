public class Instagram4 {
    public boolean func(Object o) {
        if (o == null) {
            boolean var_a = false;
            return false;
        }
        if (o == this) {
            boolean var_b = true;
            return false;
        }
        java.lang.Class<? extends java.lang.Object> var_e = o.getClass();
        java.lang.Class<? extends java.lang.Object> var_f = getClass();
        if (var_e != var_f) {
            boolean var_c = false;
            return false;
        }
        java.lang.String var_g = ((Object) o).toString();
        java.lang.String var_h = o.toString();
        boolean var_d = var_g.equals(var_h);
        return false;
    }

}
