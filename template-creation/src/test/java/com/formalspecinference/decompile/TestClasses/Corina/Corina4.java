package com.formalspecinference.decompile.TestClasses.corina;


public class Corina4 {

    protected double[][] xyz_tmp;
    protected PointObject3D vv;
    protected float rotation;
    protected View view;

    public class Location {}

    public class PointObject3D {
        public void setFromLocation(Location location) { }
        public void scale(float f) { }
        public float getX() { return 0; }
        public float getY() { return 0; }
        public float getZ() { return 0; }
    }

    public class Point3D {
        public void setX(float x) { }

        public void setY(float y) { }

        public void setZ(float z) { }
    }

    public static class Matrix {
        public static double[][] multiply(double[][] a, float b) { return null; }
    }

    public class View {
        public Dimension size;
    }

    public class Dimension {
        public int width;
        public int height;
    }

    public void project(Location location, Point3D point) {
        this.vv.setFromLocation(location);
        this.vv.scale(0.39184952F);
        this.xyz_tmp[0][0] = this.vv.getX();
        this.xyz_tmp[0][1] = this.vv.getY();
        this.xyz_tmp[0][2] = this.vv.getZ();
        this.xyz_tmp = Matrix.multiply(this.xyz_tmp, this.rotation);
        point.setX((float) (this.xyz_tmp[0][0] + (this.view.size.width / 2)));
        point.setY((float) (this.xyz_tmp[0][1] + (this.view.size.height / 2)));
        point.setZ((float) this.xyz_tmp[0][2]);
    }
}
