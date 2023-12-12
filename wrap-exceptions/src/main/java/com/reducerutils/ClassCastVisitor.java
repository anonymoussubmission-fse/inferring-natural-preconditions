package com.reducerutils;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node.TreeTraversal;
import com.github.javaparser.ast.body.MethodDeclaration;
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
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

public class ClassCastVisitor extends ModifierVisitor<Void> {
    private int lineLoc;

    public ClassCastVisitor(int lineLoc) {
        this.lineLoc = lineLoc;
    }

    private IfStmt getCastCheck(CastExpr cast) {

        Type castType = cast.getType();
        Expression exprToCast = cast.getExpression();

        InstanceOfExpr instanceOfExpr = new InstanceOfExpr(exprToCast, castType.toReferenceType().get());

        Expression condition = new UnaryExpr(new EnclosedExpr(instanceOfExpr),
                UnaryExpr.Operator.LOGICAL_COMPLEMENT);
        Statement thenStmt = (Statement) new ReturnStmt(new BooleanLiteralExpr(true));
        Statement elseStmt = (Statement) null;
        return new IfStmt(condition, thenStmt, elseStmt);
    }

    public Visitable visit(MethodDeclaration md, Void arg) {
        BlockStmt block = md.getBody().get();

        Predicate<Expression> linePredicate = f -> f.getBegin().isPresent() &&
                f.getBegin().get().line == this.lineLoc;

        List<CastExpr> casts = block.findAll(CastExpr.class, TreeTraversal.POSTORDER).stream()
                .filter(linePredicate)
                .collect(Collectors.toList());

        for (CastExpr cast : casts) {
            Utils.addCheck(cast, getCastCheck(cast));
        }

        return md;
    }

}
