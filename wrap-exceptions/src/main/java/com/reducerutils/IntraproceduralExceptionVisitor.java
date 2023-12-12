package com.reducerutils;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.Node.TreeTraversal;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.reducerutils.Utils.BlocksToAddTo;

public class IntraproceduralExceptionVisitor extends ModifierVisitor<Void> {

    private int lineLoc;
    //private String exceptionType;
    private Type exceptionTypes[];

    public IntraproceduralExceptionVisitor(JavaParser parser, int locLine, String[] exceptionTypesStr) {
        super();
        this.lineLoc = locLine;

        this.exceptionTypes = new Type[exceptionTypesStr.length];
        for (int i = 0; i < exceptionTypesStr.length; i++) {
            this.exceptionTypes[i] = parser.parseType(exceptionTypesStr[i]).getResult().get();
        }
    }

    public Visitable visit(MethodDeclaration md, Void arg) {
        BlockStmt body = md.getBody().get();
        Predicate<Expression> linePredicate = f -> f.getBegin().isPresent() &&
                f.getBegin().get().line == this.lineLoc && (f.isMethodCallExpr() || f.isObjectCreationExpr());

        List<Expression> exprs = body.findAll(Expression.class, TreeTraversal.POSTORDER)
                .stream()
                .filter(linePredicate)
                .collect(Collectors.toList());

        if (exprs.size() == 0) {
            /* 
            Predicate<Expression> p = f -> f.getBegin().isPresent() &&
                f.getBegin().get().line == this.lineLoc; //&& (f.isMethodCallExpr() || f.isObjectCreationExpr());
             List<Expression> dbg = body.findAll(Expression.class, TreeTraversal.POSTORDER)
                .stream()
                .filter(p)
                .collect(Collectors.toList());
            */
            System.out.println("NOTHING AT LINE NUMBER " + this.lineLoc);
            return null;
        }

        for (Expression expr : exprs) {
            Statement exceptionalExprParentStmt = expr.findAncestor(Statement.class).get();

            BlocksToAddTo blocksToAddTo = Utils.getBlockToAddTo(exceptionalExprParentStmt);
            BlockStmt blockToAddTo = blocksToAddTo.blockToAddTo.orElse(null);

            // We dont always want to create a new try catch. Sometimes we just need to add
            // a new clause to catch a new exception type
            if (CallWrapperUtils.needsWrap(blockToAddTo, expr)) {

                Optional<VariableDeclarationExpr> vde = CallWrapperUtils.getDeclarationIfExists(exceptionalExprParentStmt);
                /*
                 * This depends on what the parent of the exceptional call is.
                 * 
                 * If it's just an expression statement, we can just wrap the exprStmt in a try
                 * catch
                 * 
                 * If there's a variable declaration parent, then things get more tricky... We
                 * need to
                 * hoist the declaration to the top of the method, and then wrap an assignment
                 * in a try catch
                 * 
                 * If it's an assignment that's fine. Really, I think anything is fine other
                 * than a variable declaration.
                 */

                Statement stmtToTry = CallWrapperUtils.getStmtToTry(vde, expr);
                boolean needToHoist = vde.isPresent();
                if (needToHoist) {
                    CallWrapperUtils.hoistDeclaration(exceptionalExprParentStmt, vde.get());
                } 

                TryStmt tryStmt = CallWrapperUtils.createTryCatch(stmtToTry, exceptionTypes);
                int idx = Utils.getIndexToInsertAt(blockToAddTo, exceptionalExprParentStmt);

                if (needToHoist) {
                    idx += 1; //+1 because we now have the hoisted declaration
                } else {
                    blockToAddTo.remove(exceptionalExprParentStmt);
                    //REMOVE THE ORIGINAL CALL!
                }

                blockToAddTo.addStatement(idx, tryStmt);
                //blockToAddTo.remove(exceptionalExprParentStmt);

            } else {
                //Just add a catch clause to the existing tryStmt
                Statement blockParent = (Statement) blockToAddTo.getParentNode().get();
                NodeList<CatchClause> catches = blockParent.asTryStmt().getCatchClauses();
                NodeList<CatchClause> newCatches = CallWrapperUtils.createCatchClause(exceptionTypes);

                //TODO: make sure there aren't duplicates
                catches.addAll(newCatches);
            }
        }

        return md;
    }
}
