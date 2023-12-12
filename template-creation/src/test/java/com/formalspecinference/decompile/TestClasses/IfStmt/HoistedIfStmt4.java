public boolean func() {
    if (true) {
        int i = 0;
    } else {
        int var_a = bar();
        boolean var_b = foo(var_a);
        if (var_b) {
            int j = 0;
        } else {
            bar();
        }
    }
    return false;
}

