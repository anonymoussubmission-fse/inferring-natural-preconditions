package com.generator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.commons.io.IOUtils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.printer.YamlPrinter;
import com.google.gson.Gson;

public class EnumerateActions {
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

    public static BlockStmt parse(String s) throws IOException, ParseException {
        ParseResult<BlockStmt> result = parser.parseBlock(s);
        if (!result.isSuccessful()) {
            System.out.println(s);
            System.out.println(result.getProblems());
            throw new ParseException();
        }
        Optional<BlockStmt> optCu = result.getResult();
        BlockStmt cu = optCu.get();
        return cu;
    }

    public static List<CheckCandidate> generateNPEs(CompilationUnit cu) {
        NPEVisitor visitor = new NPEVisitor();
        cu.accept(visitor, null);
        return visitor.getGenerations();
    }

    public static List<String> generateCalls(CompilationUnit cu) {
        IntraproceduralExceptionVisitor visitor = new IntraproceduralExceptionVisitor(parser);
        cu.accept(visitor, null);
        return visitor.getGenerations();
    }

    public static List<String> generateCalls(BlockStmt cu) {
        IntraproceduralExceptionVisitor visitor = new IntraproceduralExceptionVisitor(parser);
        cu.accept(visitor, null);
        return visitor.getGenerations();
    }

    public static void generateAOBs(CompilationUnit cu, int lineLoc) {
        ArrayIndexOutOfBoundsVisitor visitor = new ArrayIndexOutOfBoundsVisitor(lineLoc);
        cu.accept(visitor, null);
    }

    public static void generateCasts(CompilationUnit cu, int lineLoc) {
        ClassCastVisitor visitor = new ClassCastVisitor(lineLoc);
        cu.accept(visitor, null);
    }

    public static void generateNegArray(CompilationUnit cu, int lineLoc) {
        NegativeArraySizeVisitor visitor = new NegativeArraySizeVisitor(lineLoc);
        cu.accept(visitor, null);
    }

    public static void main(String[] args) throws IOException, ParseException {
        parser = new JavaParser();
        CompilationUnit ogCU = null;

        // The input should be two lists {wraptype, lineno}

        String sampleFileName = args[0];

        try {
            ogCU = parse(Paths.get(sampleFileName));
        } catch (IOException e) {
            System.out.println("WRAPPING FAILED Couldn't open file " + sampleFileName);
            e.printStackTrace();
            return;
        } catch (ParseException e) {
            System.out.println("WRAPPING FAILED Couldn't parse " + sampleFileName);
            e.printStackTrace();
            return;
        }

        // YamlPrinter printer = new YamlPrinter(true);
        // System.out.println(printer.output(ogCU));

        CompilationUnit cu = ogCU.clone();

        List<CheckCandidate> generated = new ArrayList<CheckCandidate>();
        generated.addAll(generateNPEs(cu));

        //generated.addAll(generateCalls(cu));
        //ListIterator<String> it = generated.listIterator();    
        //if(it.hasNext()) {  
        //    BlockStmt genCU = parse(it.next()); 
        //    generated.addAll(generateCalls(genCU));
        //}

        // generateAOBs(cu);
        // generateCasts(cu);
        // generateNegArray(cu);
        String json = new Gson().toJson(generated);
        System.out.println(json);
    }
}
