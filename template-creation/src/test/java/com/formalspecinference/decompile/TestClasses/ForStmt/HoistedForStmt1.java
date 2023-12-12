public boolean func() {
    for (int i = 0; i < 10; i++) {
        int var_a = bar();
        foo(var_a);
    }
    return false;
}
