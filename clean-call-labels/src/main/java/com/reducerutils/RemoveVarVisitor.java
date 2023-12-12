package com.reducerutils;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

public class RemoveVarVisitor extends ModifierVisitor<Void> {
    private String varToRemove;

    public RemoveVarVisitor(String varToRemove) {
        this.varToRemove = varToRemove;
    }

    public Visitable visit(BlockStmt b, Void arg) {
       // NodeList<Statement> cleanStatements = new NodeList<Statement>();

        
        List<AssignExpr> assignExprs = b.findAll(AssignExpr.class);
        List<VariableDeclarationExpr> varDecls = b.findAll(VariableDeclarationExpr.class);

        for (AssignExpr assignExpr : assignExprs) {
            if (assignExpr.getTarget() instanceof NameExpr) {
                NameExpr nameExpr = assignExpr.getTarget().asNameExpr();
                if (nameExpr.getName().getIdentifier().equals(varToRemove)) {
                    assignExpr.getParentNode().get().replace(assignExpr, assignExpr.getValue());
                    //cleanStatements.add(new ExpressionStmt(assignExpr.getValue()));
                    continue;
                }
            }
        }

        for (VariableDeclarationExpr varExpr : varDecls) {
                    
            if (varExpr.getVariable(0).getNameAsString().equals(varToRemove)) {
                Optional<Expression> init = varExpr.getVariable(0).getInitializer();
                if (init.isPresent()) {
                    ///System.out.println("removing " + stmt.toString());
                    varExpr.getParentNode().get().replace(varExpr, init.get());
                    //cleanStatements.add(new ExpressionStmt(init.get()));
                    continue;
                } else {
                    varExpr.getParentNode().get().remove();
                    //System.out.println("removing " + stmt.toString());
                    continue;
                }
            }
                

        } 

        //b.setStatements(cleanStatements);
        return b;
    }
}
