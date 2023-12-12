package com.formalspecinference.decompile.TestClasses.corina;

public class Corina7 {
    public int getSelectedIndex() {
        return 0;
    }

    public Object getSelectedItem() {
        return 0;
    }

    public static class Country {
        public static String getCode(String name) {
            return null;
        }

        public static String badCode(String name) {
            return null;
        }
    }

    public String getCountry() {
        if (getSelectedIndex() == 0)
            return null;
        try {
            String result = Country.getCode((String) getSelectedItem());
            return result;
        } catch (IllegalArgumentException iee) {
            return Country.badCode((String) getSelectedItem());
        }
    }
}
