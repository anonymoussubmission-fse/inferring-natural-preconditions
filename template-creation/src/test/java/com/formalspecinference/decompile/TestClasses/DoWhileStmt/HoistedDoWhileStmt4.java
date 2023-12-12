public boolean func() {
    int a = 0;
    int b = 1;
    do {
        int var_a = bar();
        foo(var_a);
    } while (a < b);
    
    return false;
}
