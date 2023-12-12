package com.formalspecinference.decompile.TestClasses.corina;

import java.util.Arrays;

public class Corina6 {

    public static class SiteDB {
        public static SiteDB getSiteDB() {
            return new SiteDB();
        }

        public String[] getCountries() {
            //init a String[]
            return new String[0];
        }
    }

    public static class Country {
        public static String getName(String code) {
            return code;
        }

        public static String badCountry(String code) {
            return code;
        }
    }

    protected String[] getCountryNames() {
        String[] codes = SiteDB.getSiteDB().getCountries();
        String[] countries = new String[codes.length];
        for (int i = 0; i < codes.length; i++) {
            try {
                countries[i] = Country.getName(codes[i]);
            } catch (IllegalArgumentException ex) {
                countries[i] = Country.badCountry(codes[i]);
            }
        }
        Arrays.sort((Object[]) countries);
        return countries;
    }
}