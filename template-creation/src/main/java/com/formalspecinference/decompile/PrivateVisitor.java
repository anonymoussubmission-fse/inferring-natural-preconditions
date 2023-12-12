package com.formalspecinference.decompile;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.visitor.ModifierVisitor;

public class PrivateVisitor extends ModifierVisitor<Void>  { 

    @Override
    public Modifier visit(Modifier m, Void args) {
        if (m.getKeyword() == Modifier.Keyword.PRIVATE) {
            m.setKeyword(Modifier.Keyword.PROTECTED);
        }
        return m;
    }
    
}
