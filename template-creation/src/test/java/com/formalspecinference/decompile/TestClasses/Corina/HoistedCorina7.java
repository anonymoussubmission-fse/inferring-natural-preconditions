public boolean func() {
    int var_d = getSelectedIndex();
    if (var_d == 0) {
        String var_a = null;
        return false;
    }
    try {
        java.lang.Object var_e = getSelectedItem();
        String result = Country.getCode((String) var_e);
        String var_b = result;
        return false;
    } catch (IllegalArgumentException iee) {
        java.lang.Object var_f = getSelectedItem();
        String var_c = Country.badCode((String) var_f);
        return false;
    }
    return false;
}
