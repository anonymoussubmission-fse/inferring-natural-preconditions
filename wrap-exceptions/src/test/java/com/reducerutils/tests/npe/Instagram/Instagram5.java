package com.formalspecinference.decompile.TestClasses.Instagram;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Instagram5 {
    String methodUri;

    public boolean func() throws Exception {
        System.out.println(this.methodUri);
        StringBuilder sb = new StringBuilder();
        java.io.InputStream var_b = null;
        java.io.InputStreamReader var_c = new InputStreamReader(var_b);
        BufferedReader rd = new BufferedReader(var_c);
        String chunk;
        while (true) {
            java.lang.String var_d = rd.readLine();
            if (!((chunk = var_d) != null)) {
                break;
            }
            sb.append(chunk);
        }
        java.lang.String var_e = sb.toString();
        return false;
    }

}
