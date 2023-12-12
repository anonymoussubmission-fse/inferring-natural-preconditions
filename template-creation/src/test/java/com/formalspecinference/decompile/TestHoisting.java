package com.formalspecinference.decompile;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

public class TestHoisting {

    static TypeSolver solver;
    static JavaParser parser;
    static String prefix;

    private static class TestPaths {
        public final String testPath;
        public final String hoistedPath;
        
        public TestPaths(String prefix, String testDirName, int i) {
            this.testPath = Paths.get(prefix, testDirName, testDirName + i + ".java").toString();
            this.hoistedPath = Paths.get(prefix, testDirName, "Hoisted" + testDirName + i + ".java").toString();
        }
    }

    @BeforeClass
    public static void initialize() {
        List<TypeSolver> jarTypeSolvers = new ArrayList<TypeSolver>();
        jarTypeSolvers.add(new ReflectionTypeSolver());
        solver = new CombinedTypeSolver(jarTypeSolvers);
        parser = new JavaParser();
        prefix = "src/test/java/com/formalspecinference/decompile/TestClasses/";
    }

    private String readLabel(String path) throws IOException {
        FileInputStream inputStream = new FileInputStream(path);
        String hoisted;
        try {
            hoisted = IOUtils.toString(inputStream, (Charset) null);
        } finally {
            inputStream.close();
        }

        return hoisted.trim();
    }

    private MethodDeclaration getMutToTransform(String classPath) throws IOException, ParseException {
        CompilationUnit cu = Utils.parse(parser, solver, Paths.get(classPath));

        List<MethodDeclaration> allDecls = cu.findAll(MethodDeclaration.class);
        return allDecls.get(allDecls.size() - 1);
    }

    private int getNumTests(String testDirName) {
        File testDir = new File(prefix + testDirName);

        FilenameFilter ff = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(testDirName);
            }
        };

        String[] files = testDir.list(ff);
        int numFiles =  files.length;
        assert (numFiles > 0);
        return numFiles;
    }

    @Test
    public void testHoistAssertion() throws IOException, ParseException {
        
        String testDirName = "Assert";
        int num_files = getNumTests(testDirName);

        for (int i=1; i<=num_files; i++) {
            TestPaths testPaths = new TestPaths(prefix, testDirName, i);
            TemplateCreationVisitor visitor = new TemplateCreationVisitor(parser, solver);

            MethodDeclaration md = getMutToTransform(testPaths.testPath);
            md.accept(visitor, null);

            String hoisted = readLabel(testPaths.hoistedPath);

            assertEquals(md.toString(), hoisted);
        }
    }

    @Test
    public void testHoistExprStmt() throws IOException, ParseException {
        String testDirName = "ExprStmt";
        int num_files = getNumTests(testDirName);
        
        for (int i=1; i<=num_files; i++) {
            System.out.println("Test case " + i);
            TestPaths testPaths = new TestPaths(prefix, testDirName, i);

            TemplateCreationVisitor visitor = new TemplateCreationVisitor(parser, solver);

            MethodDeclaration md = getMutToTransform(testPaths.testPath);
            md.accept(visitor, null);

            String hoisted = readLabel(testPaths.hoistedPath);

            assertEquals(md.toString(), hoisted);
        }
    }

    @Test
    public void testHoistForEachStmts() throws IOException, ParseException {
        String testDirName = "ForEachStmt";
        int num_files = getNumTests(testDirName);
       
        for (int i=1; i<=num_files; i++) {
            System.out.println("Test case " + i);
            TestPaths testPaths = new TestPaths(prefix, testDirName, i);

            TemplateCreationVisitor visitor = new TemplateCreationVisitor(parser, solver);

            MethodDeclaration md = getMutToTransform(testPaths.testPath);
            md.accept(visitor, null);

            String hoisted = readLabel(testPaths.hoistedPath);

            assertEquals(md.toString(), hoisted);
        }
    }

    @Test
    public void testHoistForStmts() throws IOException, ParseException {
        String testDirName = "ForStmt";
        int num_files = getNumTests(testDirName);
        
        for (int i=1; i<=num_files; i++) {
            System.out.println("Test case " + i);

            TestPaths testPaths = new TestPaths(prefix, testDirName, i);

            TemplateCreationVisitor visitor = new TemplateCreationVisitor(parser, solver);

            MethodDeclaration md = getMutToTransform(testPaths.testPath);
            md.accept(visitor, null);

            String hoisted = readLabel(testPaths.hoistedPath);

            System.out.println(md.toString()); 
            assertEquals(md.toString(), hoisted);
        }
    }

    @Test
    public void testHoistIfStmts() throws IOException, ParseException {
        String testDirName = "IfStmt";
        int num_files = getNumTests(testDirName);

        for (int i=1; i<=num_files; i++) {
            System.out.println("Testing if stamtent test " + i);
            TestPaths testPaths = new TestPaths(prefix, testDirName, i);

            TemplateCreationVisitor visitor = new TemplateCreationVisitor(parser, solver);

            MethodDeclaration md = getMutToTransform(testPaths.testPath);
            md.accept(visitor, null);

            String hoisted = readLabel(testPaths.hoistedPath);

            assertEquals(md.toString(), hoisted);
        }
    }

    @Test
    public void testHoistTernaryStmts() throws IOException, ParseException {
        String testDirName = "Ternary";
        int num_files = getNumTests(testDirName);

        
        for (int i=1; i<=num_files; i++) {
            System.out.println("Testing Ternary Operator stamtent test " + i);
            TestPaths testPaths = new TestPaths(prefix, testDirName, i);


            TemplateCreationVisitor visitor = new TemplateCreationVisitor(parser, solver);

            MethodDeclaration md = getMutToTransform(testPaths.testPath);
            md.accept(visitor, null);
            System.out.println("LAST status of method " + md);

            String hoisted = readLabel(testPaths.hoistedPath);

            assertEquals(md.toString(), hoisted);
        }
    }

    @Test
    public void testHoistReturnStmts() throws IOException, ParseException {
        
        String testDirName = "Return";
        int num_files = getNumTests(testDirName);

        for (int i=1; i<=num_files; i++) {
            TestPaths testPaths = new TestPaths(prefix, testDirName, i);

            TemplateCreationVisitor visitor = new TemplateCreationVisitor(parser, solver);

            MethodDeclaration md = getMutToTransform(testPaths.testPath);
            md.accept(visitor, null);

            String hoisted = readLabel(testPaths.hoistedPath);

            assertEquals(md.toString(), hoisted);
        }
    }

    @Test
    public void testHoistWhileStmts() throws IOException, ParseException {
        String testDirName = "WhileStmt";
        int num_files = getNumTests(testDirName);

        for (int i=1; i<=num_files; i++) {
            System.out.println("Testing while stamtent test " + i);
            TestPaths testPaths = new TestPaths(prefix, testDirName, i);

            TemplateCreationVisitor visitor = new TemplateCreationVisitor(parser, solver);

            MethodDeclaration md = getMutToTransform(testPaths.testPath);
            md.accept(visitor, null);

            String hoisted = readLabel(testPaths.hoistedPath);

            assertEquals(md.toString(), hoisted);
        }
    }

    @Test
    public void testHoistCorina() throws IOException, ParseException {
        String testDirName = "Corina";

        int num_files = getNumTests(testDirName);

        for (int i=1; i<=num_files; i++) {
            System.out.println("Testing corina stamtent test " + i);
            
            TestPaths testPaths = new TestPaths(prefix, testDirName, i);

            TemplateCreationVisitor visitor = new TemplateCreationVisitor(parser, solver);

            MethodDeclaration md = getMutToTransform(testPaths.testPath);
            md.accept(visitor, null);

            String hoisted = readLabel(testPaths.hoistedPath);

            assertEquals(md.toString(), hoisted);
        }
    }

    @Test
    public void testHoistInstagram() throws IOException, ParseException {
        String testDirName = "Instagram";
        int num_files = getNumTests(testDirName);

        for (int i=1; i<=num_files; i++) {
            System.out.println("Testing Instagram test " + i);

            TestPaths testPaths = new TestPaths(prefix, testDirName, i);

            TemplateCreationVisitor visitor = new TemplateCreationVisitor(parser, solver);

            MethodDeclaration md = getMutToTransform(testPaths.testPath);

            md.accept(visitor, null);

            String hoisted = readLabel(testPaths.hoistedPath);

            assertEquals(md.toString(), hoisted);
        }
    }

    /* 
    @Test
    public void testHoistDoWhileStmt() throws IOException, ParseException {
        for (int i=1; i<5; i++) {
            System.out.println("Testing do while stamtent test " + i);
            String classPath = prefix + "DoWhileStmt/TestDoWhileStmt" + i + ".java";
            String hoistedPath = prefix + "DoWhileStmt/DoWhileStmtHoisted" + i + ".java";

            TemplateCreationVisitor visitor = new TemplateCreationVisitor(parser, solver);

            MethodDeclaration md = getMutToTransform(classPath);
            md.accept(visitor, null);

            String hoisted = readLabel(hoistedPath);

            assertEquals(md.toString(), hoisted);
        }
    }
    */
}
