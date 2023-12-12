package com.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

public class ClassCastVisitor extends ModifierVisitor<Void> {
    private int lineLoc;
    public ClassCastVisitor(int lineLoc) {
        this.lineLoc = lineLoc;
    }

    public void copy(List<Statement> a1, NodeList<Statement> a2) {
        for (Statement s : a2) {
            a1.add(s.clone());
        }
    }

    public Visitable visit(BlockStmt block, Void arg) {
        List<CastExpr> casts = block.findAll(CastExpr.class);

        for (CastExpr cast : casts) {

            int line = cast.getBegin().get().line;
            if (line == this.lineLoc) {
                Type castType = cast.getType();
                Expression exprToCast = cast.getExpression();

                InstanceOfExpr instanceOfExpr = new InstanceOfExpr(exprToCast, castType.toReferenceType().get());

                boolean found = false;
                Optional<BlockStmt> potentialParent = cast.findAncestor(BlockStmt.class);

                Expression condition = new UnaryExpr(new EnclosedExpr(instanceOfExpr), UnaryExpr.Operator.LOGICAL_COMPLEMENT);
                Statement thenStmt = (Statement) new ReturnStmt(new BooleanLiteralExpr(true));
                Statement elseStmt = (Statement) null;

                List<Statement> stmts = new ArrayList<Statement>();
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
                    for (CastExpr child : stmt.findAll(CastExpr.class)) {
                        if (child.equals(cast)) {
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