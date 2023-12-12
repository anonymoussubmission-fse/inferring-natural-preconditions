public class Return2 {

    public int baz(int i) {
        return i + 1;
    }

    public int bar() {
        return 1;
    }

    public int mut() {
        return bar() + 7 + baz(1);
    }
}
