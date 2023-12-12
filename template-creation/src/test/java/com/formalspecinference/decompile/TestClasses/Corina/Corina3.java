package com.formalspecinference.decompile.TestClasses.corina;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

public class Corina3 {
    public static TabSet buildTabset(String spec, int width) {
        StringTokenizer tok = new StringTokenizer(spec, " *<>^|", true);
        int n = tok.countTokens();
        List<TabStop> tabs = new ArrayList();
        int mode = 0;
        float pos = 0.0F;
        for (int i = 0; i < n; i++) {
            String t = tok.nextToken();
            if (t.trim().length() != 0)
                if (t.equals("|")) {
                    mode = 5;
                } else if (t.equals(">")) {
                    mode = 0;
                } else if (t.equals("^")) {
                    mode = 2;
                } else if (t.equals("<")) {
                    mode = 1;
                } else if (t.equals("*")) {
                    mode = 4;
                } else if (t.endsWith("%")) {
                    Float value = new Float(t.substring(0, t.length() - 1));
                    pos += value.floatValue();
                    int x = (int) (width * pos / 100.0F);
                    tabs.add(new TabStop(x, mode, 0));
                } else {
                    throw new IllegalArgumentException();
                }
        }
        return new TabSet(tabs.<TabStop>toArray(new TabStop[tabs.size()]));
    }
}
