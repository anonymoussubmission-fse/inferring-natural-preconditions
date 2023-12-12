public boolean func(Location location, Point3D point) {
    this.vv.setFromLocation(location);
    this.vv.scale(0.39184952F);
    this.xyz_tmp[0][0] = this.vv.getX();
    this.xyz_tmp[0][1] = this.vv.getY();
    this.xyz_tmp[0][2] = this.vv.getZ();
    this.xyz_tmp = Matrix.multiply(this.xyz_tmp, this.rotation);
    point.setX((float) (this.xyz_tmp[0][0] + (this.view.size.width / 2)));
    point.setY((float) (this.xyz_tmp[0][1] + (this.view.size.height / 2)));
    point.setZ((float) this.xyz_tmp[0][2]);
    return false;
}
