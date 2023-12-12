public boolean func() {
    for (int i = 0; true; i++) {
        int var_a = bar();
        boolean var_b = foo(var_a);
        if (!(var_b)) {
            break;
        }
        boolean var_c = foo(100);
        assert (var_c);
    }
    return false;
}
