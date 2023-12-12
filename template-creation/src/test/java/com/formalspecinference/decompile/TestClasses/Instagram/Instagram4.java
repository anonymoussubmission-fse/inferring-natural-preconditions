package com.formalspecinference.decompile.TestClasses.Instagram;

public class Instagram4 {
    public class Comment {
        public String id;
        
        public String getId() {
            return id;
        }
    }

    public String getId() {
        return "hi";
    }

    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o == this)
            return true;
        if (o.getClass() != getClass())
            return false;
        return ((Comment) o).getId().equals(getId());
    }
}
