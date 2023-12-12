package com.reducerutils;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node.TreeTraversal;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

public class ArrayIndexOutOfBoundsVisitor extends ModifierVisitor<Void> {
    private int lineLoc;

    public ArrayIndexOutOfBoundsVisitor(int lineLoc) {
        this.lineLoc = lineLoc;
    }

    private IfStmt getAOBCheck(ArrayAccessExpr access) {
        Expression index = access.getIndex();
        Expression arr = access.getName();
        FieldAccessExpr sizeCall = new FieldAccessExpr(arr, "length");
        Expression cond1 = new BinaryExpr(index, new IntegerLiteralExpr("0"), BinaryExpr.Operator.LESS);
        Expression cond2 = new BinaryExpr(index, sizeCall, BinaryExpr.Operator.GREATER_EQUALS);
        Expression condition = new BinaryExpr(cond1, cond2, BinaryExpr.Operator.OR);

        Statement thenStmt = (Statement) new ReturnStmt(new BooleanLiteralExpr(true));
        Statement elseStmt = (Statement) null;
        return new IfStmt(condition, thenStmt, elseStmt);
    }

    public Visitable visit(MethodDeclaration md, Void arg) {
        BlockStmt block = md.getBody().get();
             Predicate<Expression> linePredicate = f -> f.getBegin().isPresent() &&
                f.getBegin().get().line == this.lineLoc;

        List<ArrayAccessExpr> accesses = block.findAll(ArrayAccessExpr.class, TreeTraversal.POSTORDER).stream()
                .filter(linePredicate)
                .collect(Collectors.toList());

        for (ArrayAccessExpr access : accesses) {
            Utils.addCheck(access, getAOBCheck(access));
        }

        //md.setBody(block);
        return md;
    }

}
