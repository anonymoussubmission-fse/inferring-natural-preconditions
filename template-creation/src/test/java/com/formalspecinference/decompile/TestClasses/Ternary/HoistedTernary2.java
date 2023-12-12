public boolean func() {
    int var_a = bar();
    boolean var_b = foo(1);
    boolean var_c = foo(2);
    boolean b = var_a > 15 ? var_b : var_c;
    return false;
}
