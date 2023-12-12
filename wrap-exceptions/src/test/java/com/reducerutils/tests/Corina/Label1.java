package corina;

class Sample450_method extends Range {

    public Sample450_method() {
        super();
    }

    public Sample450_method(Year y1, Year y2) {
        super(y1, y2);
    }

    public Sample450_method(Year y, int span) {
        super(y, span);
    }

    public Sample450_method(String s) {
        super(s);
    }

    public boolean func() {
        int var_b;
        try {
            var_b = span();
        } catch (java.lang.NullPointerException e) {
            return true;
        }
        String var_a = "(" + this.start + " - " + this.end + ", n=" + var_b + ")";
        return false;
    }
}
