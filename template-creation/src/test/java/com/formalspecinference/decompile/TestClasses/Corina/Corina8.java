package com.formalspecinference.decompile.TestClasses.corina;

public class Corina8 {

    FloatingOptions options;
    int sample;
    MyObject left;

    public class MyObject {
        public void invalidate() { }
        public void repaint() { }
        public void add(FloatingOptions f) { }
    }

    public class Floating {
        Floating(int a, int b) {

        }

        public void run() { }
    }

    public class FloatingOptions {
        //constructor that takes two integers
        FloatingOptions() { }

        public void run() { }
    }

    public boolean remove(FloatingOptions f) {
        return false; 
    }

    public void useIndex(Floating f) {
        return;
    }


    public void actionPerformed() {
        remove(this.options);
        this.options = new FloatingOptions();
        this.left.add(this.options);
        Floating f = new Floating(this.sample, 11);
        f.run();
        this.useIndex(f);
        this.left.invalidate();
        this.left.repaint();
    }

}
