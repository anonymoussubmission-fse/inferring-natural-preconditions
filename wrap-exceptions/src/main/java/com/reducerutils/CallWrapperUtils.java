package com.reducerutils;

import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.Type;
import com.reducerutils.Utils.BlocksToAddTo;

import javassist.bytecode.analysis.ControlFlow.Block;
import javassist.bytecode.stackmap.BasicBlock.Catch;

public class CallWrapperUtils {
    
    /*
     * Sometimes, a function won't need a wrap because that exception type
     * is already being caught in a catch clause. In that case, it will return
     * false.
     */
    public static boolean needsWrap(BlockStmt blockToAddTo, Expression exceptionalExpr) {
        Node blockParentNode = blockToAddTo.getParentNode().get();
        if (!(blockParentNode instanceof Statement))
            return true;

        Statement blockParent = (Statement) blockParentNode;

        boolean hasTry = blockParent.isTryStmt();
        if (!hasTry)
            return true;

        BlockStmt blockStmt = blockParent.asTryStmt().getTryBlock();
        if (blockStmt.getStatements().size() == 1)
            return false;

        boolean isInTryBlock = blockStmt.isAncestorOf(exceptionalExpr);

        if (isInTryBlock && blockStmt.getStatements().size() == 1) {
            return false;
        } else if (isInTryBlock && blockStmt.getStatements().size() > 1) {
            // We are in a try block with multiple statements.. we want to localize
            return true;
        } else {
            // We are in a catch block
            assert (exceptionalExpr.findAncestor(CatchClause.class).isPresent());
            return false;
        }
    }

    public static Statement getStmtToTry(Optional<VariableDeclarationExpr> vde, Expression exceptionalCall) {
        if (vde.isPresent()) {
            Expression target = vde.get().getVariable(0).getNameAsExpression();
            Expression value = vde.get().getVariable(0).getInitializer().get();

            return new ExpressionStmt(new AssignExpr(target, value, AssignExpr.Operator.ASSIGN));
        } else {
            return exceptionalCall.findAncestor(Statement.class).get();
        }
    }

    public static Optional<VariableDeclarationExpr> getDeclarationIfExists(Statement parentStatement) {
        List<VariableDeclarationExpr> childDecls = parentStatement.findAll(VariableDeclarationExpr.class);

        assert (childDecls.size() <= 1);

        if (childDecls.size() == 0)
            return Optional.empty();
        else
            return Optional.of(childDecls.get(0));
    }

    public static void hoistDeclaration(Statement exceptionalExprParentStmt, VariableDeclarationExpr vde) {
        // If the expression is a child of a variable declaration, the declaration init
        // should be hoisted..
        BlocksToAddTo blocksToAddTo = Utils.getBlockToAddTo(exceptionalExprParentStmt);
        BlockStmt blockToAddTo = blocksToAddTo.blockToAddTo.orElse(null);

        // We should always have a block to add to since we have already hoisted the
        // call
        assert (blockToAddTo != null);

        //int idx = Utils.getIndexToInsertAt(blockToAddTo, exceptionalExprParentStmt);

        for (VariableDeclarator vd : vde.getVariables()) {
            vd.setInitializer((Expression) null);
        }

        //blockToAddTo.addStatement(idx, vde);
    }

    public static TryStmt createTryCatch(Statement statementToTry, Type[] exceptionTypes) {
        BlockStmt tryBlock = new BlockStmt(new NodeList<Statement>(statementToTry));

        //CatchClause catchClause = 
        NodeList<CatchClause> catchClauses = createCatchClause(exceptionTypes);
        

        return new TryStmt(tryBlock, catchClauses, null);
        /*
         * Statement newParentStatement;
         * if (declToAssign && parentStatement instanceof ExpressionStmt
         * && ((ExpressionStmt) parentStatement).getExpression() instanceof
         * VariableDeclarationExpr) {
         * VariableDeclarationExpr vde = (VariableDeclarationExpr) ((ExpressionStmt)
         * parentStatement).getExpression();
         * 
         * Expression target = vde.getVariable(0).getNameAsExpression();
         * Expression value = vde.getVariable(0).getInitializer().get();
         * 
         * newParentStatement = new ExpressionStmt(new AssignExpr(target, value,
         * AssignExpr.Operator.ASSIGN));
         * } else {
         * newParentStatement = parentStatement;
         * }
         * // TODO if there is more than one variable being declared we need multiple
         * // assignment expressions.....
         * 
         * if (declToAssign) {
         * List<VariableDeclarationExpr> childDecls =
         * newParentStatement.findAll(VariableDeclarationExpr.class);
         * for (VariableDeclarationExpr child : childDecls) {
         * Expression target = child.getVariable(0).getNameAsExpression();
         * Expression value = child.getVariable(0).getInitializer().get();
         * 
         * Node parent = child.getParentNode().get();
         * parent.replace(child, new AssignExpr(target, value, Operator.ASSIGN));
         * }
         * }
         * 
         * BlockStmt tryBlock = new BlockStmt(new
         * NodeList<Statement>(newParentStatement));
         * 
         * CatchClause catchClause = createCatchClause();
         * NodeList<CatchClause> catchClauses = new NodeList<CatchClause>(catchClause);
         * 
         * return new TryStmt(tryBlock, catchClauses, null);
         */
    }

    public static NodeList<CatchClause> createCatchClause(Type[] exceptionTypes) {

        NodeList<CatchClause> catchClauses = new NodeList<CatchClause>();

        Statement returnStatement = new ReturnStmt(new BooleanLiteralExpr(true));
        NodeList<Statement> statements = new NodeList<Statement>(returnStatement);
        BlockStmt body = new BlockStmt(statements);

        for (Type exceptionType : exceptionTypes) {
            SimpleName name = new SimpleName("e");
            Parameter parameter = new Parameter(exceptionType, name);
            CatchClause clause = new CatchClause(parameter, body.clone());
            catchClauses.add(clause);
        }

        //TODO: make sure there aren't duplicates
        return catchClauses;
    }

}
