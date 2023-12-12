import java.util.ArrayList;
import java.util.Collection;

public class ForEachStmt1 {

    public Collection<Integer> foo() {
        ArrayList<Integer> l =  new ArrayList<Integer>();
        l.add(1);
        l.add(2);
        l.add(3);
        return l;
    }

    public void mut() {
       for (Integer c : foo()) {
            System.out.println(c);
       }
    }
}
