public class ExprStmt3 {

    public boolean foo(int i) {
        return true;
    }

    public int bar() {
        return 1;
    }

    public void mut() {
        if (true) 
            foo(bar());
        else 
            foo(bar());
    }
}
