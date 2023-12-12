package com.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

public class NegativeArraySizeVisitor  extends ModifierVisitor<Void> {
    private int lineLoc;

    public NegativeArraySizeVisitor(int lineLoc) {
        this.lineLoc = lineLoc;
    }

    public void copy(List<Statement> a1, NodeList<Statement> a2) {
        for (Statement s : a2) {
            a1.add(s.clone());
        }
    }

    public Visitable visit(BlockStmt block, Void arg) {
        List<ArrayCreationLevel> levels = block.findAll(ArrayCreationLevel.class);

        for (ArrayCreationLevel level : levels) {

            int line = level.getBegin().get().line;
            //System.out.println("Access " + access.toString() + " at line " + line);
            if (line == this.lineLoc) {

                Optional<Expression> optDimension = level.getDimension();
                if (!optDimension.isPresent()) {
                    continue;
                }

                Expression dimension = optDimension.get();

                boolean found = false;
                Optional<BlockStmt> potentialParent = level.findAncestor(BlockStmt.class);

                Expression condition = new BinaryExpr(dimension, new IntegerLiteralExpr("0"), BinaryExpr.Operator.LESS);
                Statement thenStmt = (Statement) new ReturnStmt(new BooleanLiteralExpr(true));
                Statement elseStmt = (Statement) null;

                List<Statement> stmts = new ArrayList<Statement>();;
                BlockStmt blockParent;
                
                if (!potentialParent.isPresent()) {
                    copy(stmts,  block.getStatements());
                    blockParent = block;
                } else {
                    blockParent = potentialParent.get();
                    copy(stmts,  blockParent.getStatements());
                }

                for (Statement stmt : stmts) {
                    if (found) break;
                    for (ArrayCreationLevel child : stmt.findAll(ArrayCreationLevel.class)) {
                        if (child.equals(level)) {
                            int idx = stmts.indexOf(stmt);
                            if (idx == -1) {
                                throw new RuntimeException("Could not find statement " + stmt + " in block " + blockParent);
                            }
                            //System.out.println("Adding statement " + new IfStmt(condition, thenStmt, elseStmt));
                            blockParent.addStatement(idx, new IfStmt(condition, thenStmt, elseStmt));
                            found = true;
                            break;
                        }
                    }
                }
            } 
        }

        
        return block;
    }

}
