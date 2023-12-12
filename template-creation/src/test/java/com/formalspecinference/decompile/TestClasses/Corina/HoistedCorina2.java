protected static boolean func(JPanel box) {
    String description = getText("copyright");
    JPanel copyrightBlock = new JPanel();
    javax.swing.BoxLayout var_a = new BoxLayout(copyrightBlock, 1);
    copyrightBlock.setLayout(var_a);
    box.add(copyrightBlock);
    copyrightBlock.setAlignmentX(0.5F);
    java.io.StringReader var_b = new StringReader(description);
    BufferedReader r = new BufferedReader(var_b);
    while (true) {
        String line;
        try {
            line = r.readLine();
        } catch (IOException ioe) {
            break;
        }
        if (line == null)
            break;
        String subst = MessageFormat.format(line, new Object[] { "2001-2005", "Ken Harris, Aaron Hamid, Lucas Madar" });
        JLabel copyrightLabel = new JLabel(subst);
        copyrightLabel.setAlignmentX(0.5F);
        copyrightLabel.setFont(null);
        copyrightBlock.add(copyrightLabel);
    }
    return false;
}
