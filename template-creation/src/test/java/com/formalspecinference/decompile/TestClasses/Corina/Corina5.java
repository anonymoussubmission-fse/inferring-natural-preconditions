package com.formalspecinference.decompile.TestClasses.corina;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Corina5 {
    protected boolean childrenDefined;
    protected String folder;

    protected class FolderNode {
        FolderNode(String folder) {
            //this.folder = folder;
        }
    }

    protected static class NaturalSort implements Comparator {

        @Override
        public int compare(Object arg0, Object arg1) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'compare'");
        }
        
    }

    public void add(FolderNode folderNode) {
        //this.folder = folderNode.folder;
    }

    protected void defineChildNodes() {
        this.childrenDefined = true;
        File[] files = (new File(this.folder)).listFiles();
        if (files == null)
            return;
        List<String> buf = new ArrayList();
        int i;
        for (i = 0; i < files.length; i++) {
            if (files[i].isDirectory() && !files[i].isHidden())
                buf.add(files[i].getPath());
        }
        Collections.sort(buf, new NaturalSort());
        for (i = 0; i < buf.size(); i++) add(new FolderNode(buf.get(i)));
    }
}
