public boolean func() {
    ArrayList<Integer> l = new ArrayList<Integer>();
    l.add(1);
    l.add(2);
    l.add(3);
    int i = 0;
    for (Integer c : l) {
        int var_a = foo();
        int var_b = foo();
        i = var_a + var_b;
    }
    return false;
}
