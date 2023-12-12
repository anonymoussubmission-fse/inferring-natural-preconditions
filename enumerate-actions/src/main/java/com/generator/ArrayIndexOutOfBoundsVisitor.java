package com.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

public class ArrayIndexOutOfBoundsVisitor  extends ModifierVisitor<Void> {
    private int lineLoc;
    //private List<String> nullChecks;
    public ArrayIndexOutOfBoundsVisitor(int lineLoc) {
        this.lineLoc = lineLoc;
    }

    public void copy(List<Statement> a1, NodeList<Statement> a2) {
        for (Statement s : a2) {
            a1.add(s.clone());
        }
    }

    public Visitable visit(BlockStmt block, Void arg) {
        List<ArrayAccessExpr> accesses = block.findAll(ArrayAccessExpr.class);

        for (ArrayAccessExpr access : accesses) {

            int line = access.getBegin().get().line;
            //System.out.println("Access " + access.toString() + " at line " + line);
            if (line == this.lineLoc) {
                Expression index = access.getIndex();
                Expression arr = access.getName();
                FieldAccessExpr sizeCall = new FieldAccessExpr(arr, "length");

                boolean found = false;
                Optional<BlockStmt> potentialParent = access.findAncestor(BlockStmt.class);

                Expression cond1 = new BinaryExpr(index, new IntegerLiteralExpr("0"), BinaryExpr.Operator.LESS);
                Expression cond2 = new BinaryExpr(index, sizeCall, BinaryExpr.Operator.GREATER_EQUALS);
                Expression condition = new BinaryExpr(cond1, cond2, BinaryExpr.Operator.OR);

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
                    for (ArrayAccessExpr child : stmt.findAll(ArrayAccessExpr.class)) {
                        if (child.equals(access)) {
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
