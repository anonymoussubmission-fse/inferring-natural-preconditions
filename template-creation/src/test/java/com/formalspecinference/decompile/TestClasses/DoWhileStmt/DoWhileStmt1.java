public class DoWhileStmt1 {
    public static void main(String[] args) {
        // Call the method with the do-while loop
        myDoWhileLoop();
    }

    public static boolean isLessThanTen(int number) {
        return number < 10;
    }
    
    public static void myDoWhileLoop() {
        int count = 0;
        
        do {
            System.out.println("Count: " + count);
            count++;
        } while (isLessThanTen(count));
    }
}

