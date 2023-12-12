public boolean func() throws Exception {
    int var_a = bar();
    if (var_a == 0) {
        foo(1);
    }
    int var_b = bar();
    if (var_b == 100) {
        return true;
    }
    return false;
}

