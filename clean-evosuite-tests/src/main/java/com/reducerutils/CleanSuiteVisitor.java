package com.reducerutils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

public class CleanSuiteVisitor extends ModifierVisitor<Void> {
    private int numRemoved;
    CleanSuiteVisitor() {
        super();
        numRemoved = 0;
    }

    public int getNumRemoved() {
        return numRemoved;
    }

    public Visitable visit(MethodDeclaration md, Void arg) {
        BlockStmt body = md.getBody().get();
        List<TryStmt> ts = body.findAll(TryStmt.class);
        assert ts.size() <= 1;
        /*
        if (ts.size() == 0) {
            return md;
        }
        */

        boolean found = false;
        List<MethodCallExpr> calls = body.findAll(MethodCallExpr.class);
        for (MethodCallExpr call : calls) {
            //System.out.println(call.getNameAsString());
            if (call.getName().getIdentifier().equals("func")) {

                found = true;
            }

	        if (call.getName().getIdentifier().equals("assertEquals")) {
                //System.out.println("removing call" + call.toString());
                Optional<Statement> stmt = call.findAncestor(Statement.class);
                if (stmt.isPresent()) {
                    stmt.get().remove();
                }
		        //call.remove();
	        }
        }
        if (!found) {
            numRemoved += 1;
            //System.out.println("no func found");

            return null;
        }

        
        if (ts.size() == 0) {
            return md;
        }
        

        TryStmt t = ts.get(0);
        for (CatchClause c : t.getCatchClauses()) {
            Parameter p = c.getParameter();
            Type type = p.getType();
            if (type.toString().endsWith("Error")) {
                numRemoved += 1;
                return null;
            }

            NodeList<Statement> stmts = new NodeList<>();
            stmts.add(new ThrowStmt(new NameExpr(new SimpleName("e"))));
            c.setBody(new BlockStmt(stmts));
            //new Expression(new SimpleName("e"))));
        }



        return md;
    }
}
