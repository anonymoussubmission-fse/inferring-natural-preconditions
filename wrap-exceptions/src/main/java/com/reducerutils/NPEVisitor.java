package com.reducerutils;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node.TreeTraversal;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;


public class NPEVisitor extends ModifierVisitor<Void> {

    private int lineLoc;

    public NPEVisitor(int lineLoc) {
        this.lineLoc = lineLoc;
    }

    private IfStmt getNullCheck(Expression nullableExpr) {
        Expression condition = new BinaryExpr(nullableExpr.clone(), new NullLiteralExpr(), BinaryExpr.Operator.EQUALS);
        Statement thenStmt = (Statement) new ReturnStmt(new BooleanLiteralExpr(true));
        Statement elseStmt = (Statement) null;
        IfStmt nullCheck = new IfStmt(condition, thenStmt, elseStmt);

        return nullCheck;
    }

    public Visitable visit(MethodDeclaration md, Void arg) {
        BlockStmt block = md.getBody().get();
        Predicate<Expression> linePredicate = f -> f.getBegin().isPresent() &&
                f.getBegin().get().line == this.lineLoc && (f.isMethodCallExpr() || f.isFieldAccessExpr() || f.isArrayAccessExpr());

        List<Expression> exprs = block.findAll(Expression.class, TreeTraversal.POSTORDER).stream()
                .filter(linePredicate)
                .collect(Collectors.toList());
    
        if (exprs.size() == 0) {
            System.out.println("NOTHING AT LINE NUMBER " + this.lineLoc);
            return null;
        }

        for (Expression expr : exprs) {
            Expression scope = null;

            if (expr.isMethodCallExpr()) {
                MethodCallExpr call = expr.asMethodCallExpr();
                Optional<Expression> optScope = call.getScope();

                if (!optScope.isPresent()) {
                    continue;
                }

                scope = optScope.get();

                if (Utils.isStaticAccess(scope)) {
                    continue;
                }

            } else if (expr.isFieldAccessExpr()) {
                FieldAccessExpr access = expr.asFieldAccessExpr();
                scope = access.getScope();

                if (Utils.isStaticAccess(scope)) {
                    continue;
                }

            } else if (expr.isArrayAccessExpr()) {
                ArrayAccessExpr access = expr.asArrayAccessExpr();
                scope = access.getName();
                
            } else {
                throw new RuntimeException("Unknown expression type");
            }

            IfStmt nullCheck = getNullCheck(scope);
            Utils.addCheck(scope, nullCheck);
        }
        return md;
    }

}
