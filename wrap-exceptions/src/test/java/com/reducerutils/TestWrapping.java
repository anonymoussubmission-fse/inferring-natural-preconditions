package com.reducerutils;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.junit.BeforeClass;
//import junit
import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

public class TestWrapping {

    private static final String prefix = "src/test/java/com/reducerutils/tests/";
    private String[] checkTypes = { "call", "aob", "cast", "div", "negarr", "corina" };

    @BeforeClass
    public static void initialize() throws IOException {
        WrapExceptions.parser = new JavaParser();

        List<TypeSolver> jarTypeSolvers = new ArrayList<TypeSolver>();

        jarTypeSolvers.add(new JarTypeSolver("src/test/java/json.jar"));
        jarTypeSolvers.add(new ReflectionTypeSolver());
        TypeSolver solver = new CombinedTypeSolver(jarTypeSolvers);
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(solver);
        WrapExceptions.parser.getParserConfiguration().setSymbolResolver(symbolSolver);
    }

    private String extractSampleNum(String sampleFname, int checkTypeLen) {
        String sampleNum = sampleFname.substring(checkTypeLen, sampleFname.length() - 5 /* .java */);

        // assert that sampleNum is a number
        // System.out.println("sampleNum: " + sampleNum);
        // System.out.println("sampleFname: " + sampleFname);

        assert (sampleNum.matches("\\d+"));

        return sampleNum;
    }

    /*
     * testType: Assert, IfStmt, etc.
     * checkType: AOB, NPE, CALL
     */
    private List<String> getTestFiles(String testType, String checkType) {
        File testDir;
        FilenameFilter ff;
        int testTypePrefixLen;
        if (testType != null) {

            testDir = new File(prefix + checkType + "/" + testType);

            ff = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith(testType) && name.endsWith(".java");
                }
            };

            testTypePrefixLen = testType.length();

        } else {
            testDir = new File(prefix + checkType);

            ff = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith(checkType) && name.endsWith(".java");
                }
            };

            testTypePrefixLen = checkType.length();
        }

        String[] sampleFnames = testDir.list(ff);
        if (sampleFnames == null) {
            return new ArrayList<String>();
        }

        return Arrays.asList(sampleFnames).stream().map(f -> extractSampleNum(f, testTypePrefixLen))
                .collect(Collectors.toList());

    }

    private JSONObject parseJson(String fname) throws IOException {
        InputStream is = new FileInputStream(fname);
        String jsonTxt = IOUtils.toString(is, "UTF-8");
        JSONObject wrapObj = new JSONObject(jsonTxt);
        return wrapObj;
    }

    private static class TestPaths {
        public final String samplePath;
        public final String labelPath;
        public final String wrapJsonPath;

        public TestPaths(String prefix, String checkType, String testType, String sampleNum) {
            if (testType == null) {
                this.samplePath = Paths.get(prefix, checkType, checkType + sampleNum + ".java").toString();
                this.labelPath = Paths.get(prefix, checkType, "Label" + sampleNum + ".java").toString();
                this.wrapJsonPath = Paths.get(prefix, checkType, "Wrap" + sampleNum + ".json").toString();
            } else {
                this.samplePath = Paths.get(prefix, checkType, testType, testType + sampleNum + ".java").toString();
                this.labelPath = Paths.get(prefix, checkType, testType, "Label" + sampleNum + ".java").toString();
                this.wrapJsonPath = Paths.get(prefix, checkType, testType, "Wrap" + sampleNum + ".json").toString();
            }
        }
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

    @Test
    public void testCorina() throws IOException, ParseException {
        List<String> sampleNums = getTestFiles(null, "Corina");

        for (String sampleNum : sampleNums) {
            TestPaths tp = new TestPaths(prefix, "Corina", null, sampleNum);

            JSONObject wrapJson = parseJson(tp.wrapJsonPath);

            String wrappedSample = WrapExceptions.wrap(wrapJson, tp.samplePath);

            String label = readLabel(tp.labelPath);

            // strip wrappedSample of trailing whitespace
            wrappedSample = wrappedSample.replaceAll("\\s+$", "");
            assertEquals(label, wrappedSample);
            System.out.println("Test " + sampleNum + " passed");
        }

    }

    @Test
    public void testAssert() throws IOException, ParseException {
        for (String checkType : this.checkTypes) {
            System.out.println("TESTING WRAP TYPE " + checkType);

            List<String> sampleNums = getTestFiles("Assert", checkType);

            for (String sampleNum : sampleNums) {
                TestPaths tp = new TestPaths(prefix, checkType, "Assert", sampleNum);

                JSONObject wrapJson = parseJson(tp.wrapJsonPath);

                String wrappedSample = WrapExceptions.wrap(wrapJson, tp.samplePath);

                String label = readLabel(tp.labelPath);

                // strip wrappedSample of trailing whitespace
                wrappedSample = wrappedSample.replaceAll("\\s+$", "");
                assertEquals(label, wrappedSample);
                System.out.println("Test " + sampleNum + " passed");
            }
        }

    }

    @Test
    public void testExprStmt() throws IOException, ParseException {
        for (String checkType : this.checkTypes) {
            System.out.println("TESTING WRAP TYPE " + checkType);

            List<String> sampleNums = getTestFiles("ExprStmt", checkType);

            for (String sampleNum : sampleNums) {
                TestPaths tp = new TestPaths(prefix, checkType, "ExprStmt", sampleNum);

                JSONObject wrapJson = parseJson(tp.wrapJsonPath);

                String wrappedSample = WrapExceptions.wrap(wrapJson, tp.samplePath);

                String label = readLabel(tp.labelPath);

                // strip wrappedSample of trailing whitespace
                wrappedSample = wrappedSample.replaceAll("\\s+$", "");
                assertEquals(label, wrappedSample);
                System.out.println("Test " + sampleNum + " passed");
            }
        }
    }

    @Test
    public void testForEachStmt() throws IOException, ParseException {
        for (String checkType : this.checkTypes) {
            System.out.println("TESTING WRAP TYPE " + checkType);

            List<String> sampleNums = getTestFiles("ForEachStmt", checkType);

            for (String sampleNum : sampleNums) {
                TestPaths tp = new TestPaths(prefix, checkType, "ForEachStmt", sampleNum);

                JSONObject wrapJson = parseJson(tp.wrapJsonPath);

                String wrappedSample = WrapExceptions.wrap(wrapJson, tp.samplePath);

                String label = readLabel(tp.labelPath);

                // strip wrappedSample of trailing whitespace
                wrappedSample = wrappedSample.replaceAll("\\s+$", "");
                assertEquals(label, wrappedSample);
                System.out.println("Test " + sampleNum + " passed");
            }
        }
    }

    @Test
    public void testForStmt() throws IOException, ParseException {
        for (String checkType : this.checkTypes) {
            System.out.println("TESTING WRAP TYPE " + checkType);

            List<String> sampleNums = getTestFiles("ForStmt", checkType);

            for (String sampleNum : sampleNums) {
                TestPaths tp = new TestPaths(prefix, checkType, "ForStmt", sampleNum);

                JSONObject wrapJson = parseJson(tp.wrapJsonPath);

                String wrappedSample = WrapExceptions.wrap(wrapJson, tp.samplePath);

                String label = readLabel(tp.labelPath);

                // strip wrappedSample of trailing whitespace
                wrappedSample = wrappedSample.replaceAll("\\s+$", "");
                assertEquals(label, wrappedSample);
                System.out.println("Test " + sampleNum + " passed");
            }
        }
    }

    @Test
    public void testIfStmt() throws IOException, ParseException {
        for (String checkType : this.checkTypes) {
            System.out.println("TESTING WRAP TYPE " + checkType);

            List<String> sampleNums = getTestFiles("IfStmt", checkType);

            for (String sampleNum : sampleNums) {
                System.out.println("Testing " + sampleNum + "...");
                TestPaths tp = new TestPaths(prefix, checkType, "IfStmt", sampleNum);

                JSONObject wrapJson = parseJson(tp.wrapJsonPath);

                String wrappedSample = WrapExceptions.wrap(wrapJson, tp.samplePath);

                String label = readLabel(tp.labelPath);

                // strip wrappedSample of trailing whitespace
                wrappedSample = wrappedSample.replaceAll("\\s+$", "");
                assertEquals(label, wrappedSample);
                System.out.println("Test " + sampleNum + " passed");
            }
        }
    }

    @Test
    public void testInstagram() throws IOException, ParseException {
        for (String checkType : this.checkTypes) {
            System.out.println("TESTING WRAP TYPE " + checkType);

            List<String> sampleNums = getTestFiles("Instagram", checkType);

            for (String sampleNum : sampleNums) {
                System.out.println("Testing " + sampleNum + "...");

                TestPaths tp = new TestPaths(prefix, checkType, "Instagram", sampleNum);

                JSONObject wrapJson = parseJson(tp.wrapJsonPath);

                String wrappedSample = WrapExceptions.wrap(wrapJson, tp.samplePath);

                String label = readLabel(tp.labelPath);

                // strip wrappedSample of trailing whitespace
                wrappedSample = wrappedSample.replaceAll("\\s+$", "");
                assertEquals(label, wrappedSample);
                System.out.println("Test " + sampleNum + " passed");
            }
        }
    }

    @Test
    public void testTernary() throws IOException, ParseException {
        /*
         * TODO: These are not technically semantically equivalent but it's ok for
         * now...
         */
        for (String checkType : this.checkTypes) {
            System.out.println("TESTING WRAP TYPE " + checkType);

            List<String> sampleNums = getTestFiles("Ternary", checkType);

            for (String sampleNum : sampleNums) {
                TestPaths tp = new TestPaths(prefix, checkType, "Ternary", sampleNum);

                JSONObject wrapJson = parseJson(tp.wrapJsonPath);

                String wrappedSample = WrapExceptions.wrap(wrapJson, tp.samplePath);

                String label = readLabel(tp.labelPath);

                // strip wrappedSample of trailing whitespace
                wrappedSample = wrappedSample.replaceAll("\\s+$", "");
                assertEquals(label, wrappedSample);
                System.out.println("Test " + sampleNum + " passed");
            }
        }
    }

    @Test
    public void testWhileStmt() throws IOException, ParseException {
        for (String checkType : this.checkTypes) {
            System.out.println("TESTING WRAP TYPE " + checkType);

            List<String> sampleNums = getTestFiles("WhileStmt", checkType);

            for (String sampleNum : sampleNums) {
                TestPaths tp = new TestPaths(prefix, checkType, "WhileStmt", sampleNum);

                JSONObject wrapJson = parseJson(tp.wrapJsonPath);

                String wrappedSample = WrapExceptions.wrap(wrapJson, tp.samplePath);

                String label = readLabel(tp.labelPath);

                // strip wrappedSample of trailing whitespace
                wrappedSample = wrappedSample.replaceAll("\\s+$", "");
                assertEquals(label, wrappedSample);
                System.out.println("Test " + sampleNum + " passed");
            }
        }
    }

}
