package com.reducerutils;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node.TreeTraversal;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

public class DivByZeroVisitor extends ModifierVisitor<Void> {
    private int lineLoc;

    public DivByZeroVisitor(int lineLoc) {
        this.lineLoc = lineLoc;
    }

    private IfStmt getDivByZeroCheck(BinaryExpr binOp) {
            Expression cond = new BinaryExpr(binOp.getRight(), new IntegerLiteralExpr("0"), BinaryExpr.Operator.EQUALS);
            Statement thenStmt = (Statement) new ReturnStmt(new BooleanLiteralExpr(true));
            Statement elseStmt = (Statement) null;
            return new IfStmt(cond, thenStmt, elseStmt);
    }

    public Visitable visit(MethodDeclaration md, Void arg) {
        BlockStmt block = md.getBody().get();

        Predicate<Expression> linePredicate = f -> f.getBegin().isPresent() &&
                f.getBegin().get().line == this.lineLoc && f.isBinaryExpr()
                && f.asBinaryExpr().getOperator() == Operator.DIVIDE;

        List<BinaryExpr> binOps = block.findAll(BinaryExpr.class, TreeTraversal.POSTORDER).stream()
                .filter(linePredicate)
                .collect(Collectors.toList());

        for (BinaryExpr binOp : binOps) {
            Utils.addCheck(binOp, getDivByZeroCheck(binOp));
        }

        return md;
    }

}
