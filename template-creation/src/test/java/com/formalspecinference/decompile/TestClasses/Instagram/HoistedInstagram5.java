public boolean func() throws Exception {
    System.out.println(this.methodUri);
    StringBuilder sb = new StringBuilder();
    java.io.InputStream var_b = performRequest();
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
    RequestResponse var_a = new RequestResponse(var_e);
    return false;
}
