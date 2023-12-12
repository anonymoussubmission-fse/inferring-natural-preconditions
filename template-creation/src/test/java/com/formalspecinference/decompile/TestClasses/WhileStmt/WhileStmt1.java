public class WhileStmt1 {

    public boolean foo(int i) {
        return true;
    }

    public int bar() {
        return 1;
    }

    public int baz() {
        return 1;
    }


    public int mut() {
	 int a = 5;
         while (a < 10) {
		a = baz() + baz();	
	 }
	 return a;
    }
}
