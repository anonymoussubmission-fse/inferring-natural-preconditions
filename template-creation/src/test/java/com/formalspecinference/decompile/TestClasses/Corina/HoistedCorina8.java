public boolean func() {
    remove(this.options);
    this.options = new FloatingOptions();
    this.left.add(this.options);
    Floating f = new Floating(this.sample, 11);
    f.run();
    this.useIndex(f);
    this.left.invalidate();
    this.left.repaint();
    return false;
}
