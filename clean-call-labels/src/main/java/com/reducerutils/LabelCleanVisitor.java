package com.reducerutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.metamodel.AssignExprMetaModel;

public class LabelCleanVisitor extends ModifierVisitor<Void> {
    private Map<String, Integer> uses;

    public LabelCleanVisitor() {
        uses = new HashMap<String, Integer>();
    }

    public Map<String, Integer> getUses() {
        return uses;
    }

    public Visitable visit(VariableDeclarator d, Void arg) {
        String strName = d.getNameAsString();
        if (strName.contains("var_")) {

            //System.out.println(d.toString());

            uses.put(strName, 0);

        }
        super.visit(d, arg);
        return d;
    }

    public Visitable visit(NameExpr name, Void arg) {
        String strName = name.getName().getIdentifier().toString();
        if (!name.getName().getIdentifier().contains("var_"))  return name;

        Optional<AssignExpr> assignExprOpt = name.findAncestor(AssignExpr.class);
        if (assignExprOpt.isPresent()) {
            AssignExpr assign = assignExprOpt.get();
            if (assign.getTarget().isAncestorOf(name) || assign.getTarget().equals(name)) {
                //System.out.println(assign.toString());

                //System.out.println("yeah" + strName);
                return name;
            }

        }

        Integer curValue = uses.get(strName);
        //System.out.println(name.getParentNode().get());

         uses.put(strName, curValue + 1);
        return name;
    }

}
