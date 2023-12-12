package com.formalspecinference.decompile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

public class Utils {

    public static final boolean DEBUG = false;
    
    public static String getPackage(CompilationUnit cu) throws IOException, ParseException {
        return cu.getPackageDeclaration().get().getName().toString();
    }

    public static String getImports(CompilationUnit cu) {
        NodeList<ImportDeclaration> importList = cu.getImports();
        String imports = "";
    
        for (ImportDeclaration imp : importList) {
            imports += imp.toString() + "\n";
        }   
        
        return imports;
    }


    public static CompilationUnit parse(JavaParser parser, TypeSolver solver, Path p) throws IOException, ParseException {
        if (solver != null) {
            JavaSymbolSolver symbolSolver = new JavaSymbolSolver(solver);
            parser.getParserConfiguration().setSymbolResolver(symbolSolver);
        }

        ParseResult<CompilationUnit> result = parser.parse(p);
        if (!result.isSuccessful()) {
            throw new ParseException();
        }
        Optional<CompilationUnit> optCu = result.getResult();
        CompilationUnit cu = optCu.get();
        return cu;
    }

    public static String getConstructorText(List<ConstructorDeclaration> cus, String ogClassName, int sampleNum) {
        String constructors = "";
        for (ConstructorDeclaration c : cus) {
            if (!c.getNameAsString().equals(ogClassName)) {
                continue;
            }
            String decString = c.getDeclarationAsString();
            if (DEBUG) {
                System.out.println("Constructor: " + decString);
            }

            ArrayList<String> inputs = new ArrayList<String>();
            for (Parameter c_param : c.getParameters()) {
                inputs.add(c_param.getNameAsString());
            }

            constructors += decString.replace(ogClassName, "Sample" + String.valueOf(sampleNum) + "_method") +
                    "{ \n" + "super(" + String.join(", ", inputs) + ");\n}";
        }
        return constructors;
    } 

    public static boolean isElseIf(IfStmt parentOfExprToHoist, Expression exprToHoist) {
        Node parent = parentOfExprToHoist.getParentNode().get();
        if (!(parent instanceof IfStmt)) {
            return false;
        }

        Optional<Statement> elseStmt = ((IfStmt) parent).getElseStmt();
        if (!elseStmt.isPresent()) { 
            return false;
        }
    
        return (elseStmt.get() instanceof IfStmt) && elseStmt.get().asIfStmt().getCondition().isAncestorOf(exprToHoist);
    }


    public static Node getNearest(List<Node> ancestors, Statement child) {
        if (ancestors.size() == 1) return ancestors.get(0);

        Node nearestAncestor = ancestors.get(0);
        for (Node ancestor: ancestors) {
            if (nearestAncestor.isAncestorOf(ancestor)) {
                nearestAncestor = ancestor;
            }
        }

        return nearestAncestor;
        
    } 

    /*
     * public static void processArgs(CompilationUnit cu, Path p) {
        ArgsVisitor visitor = new ArgsVisitor(p.toString(), p.toString());

        cu.getTypes().forEach(type -> type.getChildNodes().forEach(c -> {
            c.accept(visitor, null);

        }));

        System.out.println("Invalid count " + visitor.getInvalidCount() + " Valid count " + visitor.getValidCount());
        invalidMethods += visitor.getInvalidCount();
        validMethods += visitor.getValidCount();
    }


    public static void countSamples(Path p) throws IOException, ParseException {
        CompilationUnit cu = Utils.parse(parser, null, p);
        processArgs(cu, p);
     }



    public static List<CallableDeclaration> getGoodMethods(CompilationUnit cu, Path p)
            throws IOException, ParseException {
        ArgsVisitor visitor = new ArgsVisitor(p.toString(), p.toString());

        cu.accept(visitor, null);

        return visitor.getMethodNodes();
    }
     */
}
