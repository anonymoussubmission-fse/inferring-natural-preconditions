package com.formalspecinference.decompile.TestClasses.Instagram;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Instagram5 {
    String methodUri;

    public class RequestResponse {
        public RequestResponse(String s) {
        }
    }

    public InputStream performRequest() {
        return null;
    }

    public RequestResponse call() throws Exception {
        System.out.println(this.methodUri);
        StringBuilder sb = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(performRequest()));
        String chunk;
        while ((chunk = rd.readLine()) != null) sb.append(chunk);
        return new RequestResponse(sb.toString());
    }
}
