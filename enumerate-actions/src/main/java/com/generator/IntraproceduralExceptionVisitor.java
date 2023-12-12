package com.generator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.expr.AssignExpr.Operator;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.metamodel.TryStmtMetaModel;

import org.apache.commons.io.serialization.ClassNameMatcher;

public class IntraproceduralExceptionVisitor extends VoidVisitorAdapter<Void>  {


    private JavaParser parser;
    private List<String> possibleBlocks;
    public IntraproceduralExceptionVisitor(JavaParser parser) {
        super();
        this.parser = parser;
        possibleBlocks = new ArrayList<String>();
    }

    public List<String> getGenerations() {
        return possibleBlocks;
    }

    private boolean needsWrap(Node n) {
        if (n.findAncestor(TryStmt.class).isPresent()) {
            TryStmt tryStmtParent = n.findAncestor(TryStmt.class).get();
            
            if (!tryStmtParent.getTryBlock().isAncestorOf(n)) return true;
        
            //TODO: Make sure there is a general exception catch
            NodeList<CatchClause> catches = tryStmtParent.getCatchClauses();
            
            for (CatchClause _catch : catches) {
                String thisExceptionType = _catch.getParameter().getType().toString();

                if (thisExceptionType.equals("Exception")) {
                    return false;
                }
            }
        }


        if (n.findAncestor(ObjectCreationExpr.class).isPresent()) return false;
        if (n.findAncestor(MethodCallExpr.class).isPresent()) return false;

        return true;
    }

    public void visit(MethodDeclaration md, Void arg) {
        BlockStmt body = md.getBody().get();

        List<Expression> exprs = body.findAll(Expression.class);
        //List<ObjectCreationExpr> oce = body.findAll(ObjectCreationExpr.class);

        
        for (Expression expr : exprs) {

            if (!(expr instanceof MethodCallExpr || expr instanceof ObjectCreationExpr)) continue;

            //System.out.println(expr.toString() + " needs wrap? " + needsWrap(expr) + " lineno " + expr.getBegin().get().line);

                
            if (!needsWrap(expr)) continue;

            Statement parentStatement = expr.findAncestor(Statement.class).get();
            Optional<Node> grandparent = parentStatement.getParentNode();
            if (!grandparent.isPresent()) continue;


            fixScopeIssues(body, parentStatement);

            // We dont always want to create a new try catch. Sometimes we njust need to add a new clause to catch a new exception type
            if (!expr.findAncestor(TryStmt.class).isPresent() || 
                (expr.findAncestor(TryStmt.class).isPresent() && !expr.findAncestor(TryStmt.class).get().getTryBlock().isAncestorOf(expr)))
            {
                TryStmt tryStmt = createTryCatch(parentStatement, true);
                possibleBlocks.add(tryStmt.toString());
                Node stmtToReplace = grandparent.get();
                if (stmtToReplace instanceof BlockStmt) {
                    NodeList<Statement> stmts = ((BlockStmt) stmtToReplace).getStatements();
                    stmts.replace(parentStatement, tryStmt);
                } else {
                    stmtToReplace.replace(parentStatement, tryStmt);
                }
            } else {
                TryStmt tryStmt = expr.findAncestor(TryStmt.class).get();
                NodeList<CatchClause> catches = tryStmt.getCatchClauses();
                CatchClause newCatch = createCatchClause();
                catches.add(newCatch);
            }

            
        }

        
    }

    private TryStmt createTryCatch(Statement parentStatement, boolean declToAssign) {
        Statement newParentStatement; 
        if (declToAssign && parentStatement instanceof ExpressionStmt && ((ExpressionStmt) parentStatement).getExpression() instanceof VariableDeclarationExpr) {
            VariableDeclarationExpr vde = (VariableDeclarationExpr) ((ExpressionStmt) parentStatement).getExpression();
           
            Expression target = vde.getVariable(0).getNameAsExpression();
            Expression value = vde.getVariable(0).getInitializer().get();
            
            newParentStatement = new ExpressionStmt(new AssignExpr(target, value, AssignExpr.Operator.ASSIGN));
        } else {
            newParentStatement = parentStatement;
        }
        //TODO if there is more than one variable being declared we need multiple assignment expressions..... 
        
        if(declToAssign) {
            List<VariableDeclarationExpr> childDecls = newParentStatement.findAll(VariableDeclarationExpr.class);
            for (VariableDeclarationExpr child : childDecls) {
                Expression target = child.getVariable(0).getNameAsExpression();
                Expression value = child.getVariable(0).getInitializer().get();
        
                Node parent = child.getParentNode().get();
                parent.replace(child, new AssignExpr(target, value, Operator.ASSIGN));
            }
        }

        BlockStmt tryBlock = new BlockStmt(new NodeList<Statement>(newParentStatement));

        
        CatchClause catchClause = createCatchClause();
        NodeList<CatchClause> catchClauses = new NodeList<CatchClause>(catchClause);

        return new TryStmt(tryBlock, catchClauses, null);
    }

    private CatchClause createCatchClause() {
        Statement returnStatement = new ReturnStmt(new BooleanLiteralExpr(true));
        NodeList<Statement> statements = new NodeList<Statement>(returnStatement);
        BlockStmt body = new BlockStmt(statements);

        Type exceptionType = parser.parseType("Exception").getResult().get();
        SimpleName name = new SimpleName("e");
        Parameter parameter = new Parameter(exceptionType, name);
        return new CatchClause(parameter, body);
    }

    private void fixScopeIssues(BlockStmt body, Statement parentStatement) {
        List<VariableDeclarationExpr> childDecls = parentStatement.findAll(VariableDeclarationExpr.class);
        for (VariableDeclarationExpr child : childDecls) {
            VariableDeclarationExpr vde = child.clone();
            for (VariableDeclarator vd : vde.getVariables()) {
                vd.setInitializer((Expression) null);
            }

            if (body.getStatements().contains(parentStatement)) {
                body.getStatements().addBefore(new ExpressionStmt(vde), parentStatement);
            
            } else {
                body.getStatements().add(0, new ExpressionStmt(vde));
            }
        }
    }
}
