package corina;
import corina.core.App;

import corina.formats.WrongFiletypeException;

import java.io.File;

import java.io.IOException;

import java.util.Map;

 class Sample1608_method extends Element { 
public Sample1608_method(String filename){ 
super(filename);
}public Sample1608_method(String filename, boolean active){ 
super(filename, active);
}
public boolean func(Object o) {
    int var_a = this.filename.compareTo(((Element) o).filename);
    return false;
} 
}