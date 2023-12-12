public class Ternary1 {

    public boolean mut(Boolean[] foo, int bar) {
        if (bar < 0 || bar >= foo.length)
            return true;
        boolean var_a = foo[bar] ? false : true;
        return false;
    }
}
