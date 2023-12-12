package com.reducerutils;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wraps {
    private Wrap[] wraps;

    public Wraps(Wrap[] wraps) {
        this.wraps = processWraps(wraps);
    }

    private Wrap[] processWraps(Wrap[] wraps) {
        // merge wraps with same type and line number
        ArrayList<Wrap> mergedWraps = new ArrayList<Wrap>();

        Map<String, ArrayList<Wrap>> callMap = new HashMap<String, ArrayList<Wrap>>();
        for (Wrap wrap : wraps) {
            if (wrap.getWrapType() != WrapType.CALL) {
                mergedWraps.add(wrap);
                continue;
            }

            String key = wrap.getWrapType().toString() + Integer.toString(wrap.getLineNo());
            if (callMap.containsKey(key)) {
                callMap.get(key).add(wrap);
            } else {
                ArrayList<Wrap> wrapList = new ArrayList<Wrap>();
                wrapList.add(wrap);
                callMap.put(key, wrapList);
            }
        }

        ArrayList<Wrap> mergedCallWraps = new ArrayList<Wrap>();
        for (ArrayList<Wrap> callList : callMap.values()) {
            if (callList.size() == 1) {
                mergedWraps.add(callList.get(0));
            } else {
                String[] exceptionTypes = new String[callList.size()];
                for (int i = 0; i < callList.size(); i++) {
                    exceptionTypes[i] = callList.get(i).getExceptionType();
                }
                Wrap mergedWrap = new Wrap(callList.get(0).getLineNo(), exceptionTypes, WrapType.CALL);
                mergedCallWraps.add(mergedWrap);
            }
        }

        mergedWraps.addAll(mergedCallWraps);
        // turn the arrayList mergedWraps into an array
        Wrap[] mergedWrapsArr = new Wrap[mergedWraps.size()];
        mergedWrapsArr = mergedWraps.toArray(mergedWrapsArr);
        return mergedWrapsArr;
    }

    public Wrap[] getWraps() {
        return wraps;
    }
}
