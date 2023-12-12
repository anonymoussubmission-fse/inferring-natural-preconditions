package com.formalspecinference.decompile;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

public class ClassnameVisitor extends ModifierVisitor<Void>  {
    String newName;
    ClassnameVisitor(String name) {
        super();
        newName = name;

    }

    public Visitable visit(ConstructorDeclaration cd, Void arg) {
        cd.setName(newName);
        return cd;
    }

    public Visitable visit(ClassOrInterfaceDeclaration c, Void arg) {
        super.visit(c, null);
        c.setName(newName);
        return c;
    }

}
