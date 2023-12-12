public class Ternary2 {

    public boolean mut(Long[] foo, int bar, int z) {
        if (bar < 0 || bar >= foo.length)
            return true;
        if (15 < 0 || 15 >= foo.length)
            return true;
        if (z == 0)
            return true;
        if (bar / z < 0 || bar / z >= foo.length) {
            return true;
        }
        Long var_a = foo[bar] > 15 ? foo[15] : foo[bar / z];
        return false;
    }
}
