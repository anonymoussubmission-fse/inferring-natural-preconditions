public boolean func() {
    if (true) {
        int var_a = bar();
        foo(var_a);
    } else {
        int var_b = bar();
        foo(var_b);
    }
    return false;
}
