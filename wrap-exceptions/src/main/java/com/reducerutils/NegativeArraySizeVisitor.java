package com.reducerutils;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.Node.TreeTraversal;
import com.github.javaparser.ast.body.MethodDeclaration;
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

public class NegativeArraySizeVisitor extends ModifierVisitor<Void> {
    private int lineLoc;

    public NegativeArraySizeVisitor(int lineLoc) {
        this.lineLoc = lineLoc;
    }

    private IfStmt getNegArrayCheck(ArrayCreationLevel level) {
        Expression dimension = level.getDimension().get();

        Expression condition = new BinaryExpr(dimension, new IntegerLiteralExpr("0"), BinaryExpr.Operator.LESS);
        Statement thenStmt = (Statement) new ReturnStmt(new BooleanLiteralExpr(true));
        Statement elseStmt = (Statement) null;
        return new IfStmt(condition, thenStmt, elseStmt);
    }

    public Visitable visit(MethodDeclaration md, Void arg) {
        BlockStmt block = md.getBody().get();
        Predicate<ArrayCreationLevel> linePredicate = f -> f.getBegin().isPresent() &&
                f.getBegin().get().line == this.lineLoc && f.getDimension().isPresent();

        List<ArrayCreationLevel> levels = block.findAll(ArrayCreationLevel.class, TreeTraversal.POSTORDER).stream()
                .filter(linePredicate)
                .collect(Collectors.toList());

        for (ArrayCreationLevel level : levels) {
            Utils.addCheck(level, getNegArrayCheck(level));
        }

        return md;
    }

}
