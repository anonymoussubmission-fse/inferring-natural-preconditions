package com.reducerutils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

public class CleanEvo {
    public static JavaParser parser; 

    public static CompilationUnit parse(Path p) throws IOException, ParseException {
        ParseResult<CompilationUnit> result = parser.parse(p);
        if (!result.isSuccessful()) {
            throw new ParseException();
        } 
        Optional<CompilationUnit> optCu = result.getResult();
        CompilationUnit cu = optCu.get();
        return cu;
    }

    public static int removeErrorCases(CompilationUnit cu) {
        CleanSuiteVisitor visitor = new CleanSuiteVisitor();
        cu.accept(visitor, null);

        return visitor.getNumRemoved();
    }
    
    //open an evosuite test file
    //parse it to find all of the methods that catch an Error
    //remove those 
    //rewrite the file 
    
    public static void main(String[] args) {
        parser = new JavaParser();
        CompilationUnit ogCU = null;

        String evosuiteFileName = args[0];
        try {
            ogCU = parse(Paths.get(evosuiteFileName));
        } catch (IOException e) {
            System.out.println("CLEANING FAILED Couldn't open file " + evosuiteFileName);
            e.printStackTrace();
            return;
        } catch (ParseException e) {
            System.out.println("CLEANING FAILED Couldn't parse " + evosuiteFileName);
            e.printStackTrace();
            return;
        }

        CompilationUnit cu = ogCU.clone();
        int numRemoved = removeErrorCases(cu);

        //System.out.println("AFTER");
        System.out.println(cu);
        //System.out.println("removed " + numRemoved + " test cases");


        //TODO: save the new file somewhere??
    }
}

