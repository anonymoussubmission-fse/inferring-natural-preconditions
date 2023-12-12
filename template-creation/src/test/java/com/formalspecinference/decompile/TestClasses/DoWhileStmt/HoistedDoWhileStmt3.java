public boolean func() {
    int a = 0;
    int b = 1;
    int var_a;
    boolean var_b;
    do {
        a += 1;
        int var_a = bar();
        boolean var_b = foo(var_a);
    } while (var_b);
    return false;
}
