package com.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.Position;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.SuperExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class NPEVisitor extends VoidVisitorAdapter<Void>  {

    private List<CheckCandidate> checkCandidates;
    private List<String> nullChecks;
    public NPEVisitor() {
        nullChecks = new ArrayList<String>();
        checkCandidates = new ArrayList<CheckCandidate>();
    }

    public void copy(List<Statement> a1, NodeList<Statement> a2) {
        for (Statement s : a2) {
            a1.add(s.clone());
        }
    }

    public List<CheckCandidate> getGenerations() {
        return checkCandidates;
    }

    public void visit(BlockStmt block, Void arg) {
        List<MethodCallExpr> calls = block.findAll(MethodCallExpr.class);
        List<FieldAccessExpr> accesses = block.findAll(FieldAccessExpr.class);
        
        //YamlPrinter printer = new YamlPrinter(true);
        //System.out.println(printer.output(block));

        //System.out.println("Visiting " + block.toString());
        for (MethodCallExpr call : calls) {
            int line = call.getBegin().get().line;
           Optional<Expression> optScope = call.getScope();

            if (!optScope.isPresent()) {
                continue;
            }

            Expression scope = optScope.get();
            if (nullChecks.contains(scope.toString())) {
                continue;
            }

            if (scope instanceof ThisExpr || scope instanceof SuperExpr) continue;

            boolean found = false;
            Optional<BlockStmt> potentialParent = call.findAncestor(BlockStmt.class);

            Expression condition = new BinaryExpr(scope.clone(), new NullLiteralExpr(), BinaryExpr.Operator.EQUALS);
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
                for (MethodCallExpr child : stmt.findAll(MethodCallExpr.class)) {
                    if (child.equals(call)) {
                        int index = stmts.indexOf(stmt);
                        if (index == -1) {
                            throw new RuntimeException("Could not find statement " + stmt + " in block " + blockParent);
                        }

                        nullChecks.add(optScope.get().toString());
                        IfStmt check = new IfStmt(condition, thenStmt, elseStmt);
                        //blockParent.addStatement(index, check);
                        checkCandidates.add(new CheckCandidate(check.toString(), line));

                        found = true;
                        break;
                    }
                }
            }
        }

        for (FieldAccessExpr access : accesses) {
            int line = access.getBegin().get().line;

            Optional<Position> begin = access.getBegin();
            if (!begin.isPresent()) continue;
            //System.out.println("Visiting " + access.toString());

            Expression scope = access.getScope();
            if (scope instanceof ThisExpr || scope instanceof SuperExpr) continue;

            if (nullChecks.contains(scope.toString())) {
                continue;
            }
            Expression condition = new BinaryExpr(scope, new NullLiteralExpr(), BinaryExpr.Operator.EQUALS);
            Statement thenStmt = (Statement) new ReturnStmt(new BooleanLiteralExpr(true));
            Statement elseStmt = (Statement) null;

            ///add the ifStmt to the method!

            boolean found = false;
            List<Statement> stmts = new ArrayList<Statement>(block.getStatements().size()); 
            BlockStmt blockParent;
            Optional<BlockStmt> potentialParent = access.findAncestor(BlockStmt.class);

            if (!potentialParent.isPresent()) {
                copy(stmts,  block.getStatements());
                blockParent = block;
            } else {
                blockParent = potentialParent.get();
                copy(stmts,  blockParent.getStatements());
            }
            for (Statement stmt : stmts) {
                if (found) break;
                for (FieldAccessExpr child : stmt.findAll(FieldAccessExpr.class)) {
                    if (child.equals(access)) {
                        int index = blockParent.getStatements().indexOf(stmt);
                        if (index == -1) {
                            throw new RuntimeException("Could not find statement " + stmt + " in block " + blockParent);
                        }
                        //System.out.println("Adding statement " + new IfStmt(condition, thenStmt, elseStmt));
                        nullChecks.add(access.getScope().toString());

                        IfStmt check = new IfStmt(condition, thenStmt, elseStmt);
                        //blockParent.addStatement(index, check);
                        checkCandidates.add(new CheckCandidate(check.toString(), line));
                        found = true;
                        break;
                    }
                }
            }
        }
        
    }

}
