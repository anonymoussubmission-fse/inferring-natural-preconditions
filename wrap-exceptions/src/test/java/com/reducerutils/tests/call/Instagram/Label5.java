package com.reducerutils.tests.call.Instagram;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Instagram5 {

    String methodUri;

    public boolean func() throws Exception {
        System.out.println(this.methodUri);
        StringBuilder sb;
        try {
            sb = new StringBuilder();
        } catch (java.lang.NullPointerException e) {
            return true;
        }
        java.io.InputStream var_b = null;
        java.io.InputStreamReader var_c = new InputStreamReader(var_b);
        BufferedReader rd;
        try {
            rd = new BufferedReader(var_c);
        } catch (java.lang.NullPointerException e) {
            return true;
        }
        String chunk;
        while (true) {
            if (rd == null)
                return true;
            java.lang.String var_d = rd.readLine();
            if (!((chunk = var_d) != null)) {
                break;
            }
            try {
                sb.append(chunk);
            } catch (java.lang.NullPointerException e) {
                return true;
            }
        }
        java.lang.String var_e = sb.toString();
        return false;
    }
}
