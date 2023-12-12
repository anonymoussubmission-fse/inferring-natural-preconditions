public boolean func() {
    for (int i = 0; true; i++) {
        int var_a = bar();
        if (!(i < var_a))
            break;
        boolean var_b = foo(i);
        assert (var_b);
    }
    return false;
} 
