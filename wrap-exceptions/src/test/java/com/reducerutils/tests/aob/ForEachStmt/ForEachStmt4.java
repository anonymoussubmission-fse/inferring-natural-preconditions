package com.reducerutils.tests.aob.ForEachStmt;
import java.util.ArrayList;

public class ForEachStmt4 {
    
    public boolean mut(int foo) {
		ArrayList<Integer>[] l = new ArrayList[foo];
		l[0] = new ArrayList<Integer>();
		l[1] = new ArrayList<Integer>();
		l[2] = new ArrayList<Integer>();
		l[3] = new ArrayList<Integer>();

		l[0].add(1);
		l[1].add(2);
		l[2].add(3);
		l[3].add(4);

		int i = 0;
		for (Integer c : l[foo+10])
            i = c + c + 100;
		
		return false;
	}
}
