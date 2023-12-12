package com.typeresolver;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.resolution.SymbolResolver;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

public class ResolveSignature {
    public static JavaParser parser;

    public static String getQualifiedType(Parameter param) {
        return param.getType().resolve().asReferenceType().getQualifiedName().replace(".","/");
    }

    public static CompilationUnit parse(TypeSolver solver, Path p) throws IOException, ParseException {
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(solver);
        parser.getParserConfiguration().setSymbolResolver(symbolSolver);
        
        ParseResult<CompilationUnit> result = parser.parse(p);
        if (!result.isSuccessful()) {
            throw new ParseException();
        } 
        Optional<CompilationUnit> optCu = result.getResult();
        CompilationUnit cu = optCu.get();
        return cu;
    }

    public static void main(String[] args) throws IOException, ParseException {
        parser = new JavaParser();
        List<TypeSolver> jarTypeSolvers = new ArrayList<TypeSolver>();

        String oldjar = args[0];
        String newjar = args[1];
        String fileToParse = args[2];

        String[] libjars;
        if (args.length > 3) {
            libjars = args[3].split(":");;
        } else {
            libjars = new String[0];
        }

        for (String libjar : libjars) {
            jarTypeSolvers.add(new JarTypeSolver(libjar));
        }

        jarTypeSolvers.add(new JarTypeSolver(oldjar));
        jarTypeSolvers.add(new JarTypeSolver(newjar));
        jarTypeSolvers.add(new ReflectionTypeSolver());

        CombinedTypeSolver solver = new CombinedTypeSolver(jarTypeSolvers);

        CompilationUnit cu = parse(solver, Paths.get(fileToParse));
        cu.findAll(MethodDeclaration.class).forEach(m -> {
            if (m.getName().toString().equals("func")) {
                m.getParameters().forEach(p -> {
                    String qualType = getQualifiedType(p);
                    if (qualType.contains("/")) qualType = "L" + qualType + ";";
                    System.out.println(qualType);
                });
            }
        });
    }
}
