public class Return1 {

    public boolean foo(int i) {
        return true;
    }

    public int bar() {
        return 1;
    }

    public int mut() {
        return bar();
    }
}
