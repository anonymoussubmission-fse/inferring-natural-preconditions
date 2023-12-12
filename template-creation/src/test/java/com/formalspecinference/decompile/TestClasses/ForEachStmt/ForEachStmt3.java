import java.util.ArrayList;
import java.util.Collection;

public class ForEachStmt3 {

	public int foo() {
		return 1;
	}

	public void mut() {
		ArrayList<Integer> l =  new ArrayList<Integer>();
		l.add(1);
		l.add(2);
		l.add(3);

		int i = 0;
		for (Integer c : l) i = foo() + foo(); 
	}
}
