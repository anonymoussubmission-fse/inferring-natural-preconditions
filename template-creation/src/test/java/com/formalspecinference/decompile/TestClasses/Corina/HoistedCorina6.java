protected boolean func() {
    SiteDB var_b = SiteDB.getSiteDB();
    String[] codes = var_b.getCountries();
    String[] countries = new String[codes.length];
    for (int i = 0; i < codes.length; i++) {
        try {
            countries[i] = Country.getName(codes[i]);
        } catch (IllegalArgumentException ex) {
            countries[i] = Country.badCountry(codes[i]);
        }
    }
    Arrays.sort((Object[]) countries);
    String[] var_a = countries;
    return false;
}
