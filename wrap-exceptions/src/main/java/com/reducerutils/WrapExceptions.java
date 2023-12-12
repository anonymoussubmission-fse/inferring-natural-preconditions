package com.reducerutils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.nio.file.Files;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.commons.io.IOUtils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.printer.YamlPrinter;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.SourceRoot;

public class WrapExceptions {
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

    public static List<CompilationUnit> parse_project(Path p) throws IOException, ParseException {
        SourceRoot sourceRoot = new SourceRoot(p);
        sourceRoot.tryToParse();

        return sourceRoot.getCompilationUnits();
    }

    public static void wrapNPE(CompilationUnit cu, int lineLoc) {
        NPEVisitor visitor = new NPEVisitor(lineLoc);
        cu.accept(visitor, null);
    }

    public static void wrapCall(CompilationUnit cu, int lineLoc, String[] exceptionType) {
        IntraproceduralExceptionVisitor visitor = new IntraproceduralExceptionVisitor(parser, lineLoc, exceptionType);
        cu.accept(visitor, null);
    }

    public static void wrapArrayAccess(CompilationUnit cu, int lineLoc) {
        ArrayIndexOutOfBoundsVisitor visitor = new ArrayIndexOutOfBoundsVisitor(lineLoc);
        cu.accept(visitor, null);
    }

    public static void wrapCast(CompilationUnit cu, int lineLoc) {
        ClassCastVisitor visitor = new ClassCastVisitor(lineLoc);
        cu.accept(visitor, null);
    }

    public static void wrapNegArray(CompilationUnit cu, int lineLoc) {
        NegativeArraySizeVisitor visitor = new NegativeArraySizeVisitor(lineLoc);
        cu.accept(visitor, null);
    }

    public static void wrapDivZero(CompilationUnit cu, int lineLoc) {
        DivByZeroVisitor visitor = new DivByZeroVisitor(lineLoc);
        cu.accept(visitor, null);
    }

    public static String wrap(JSONObject wrapObj, String sampleFileName) {
        CompilationUnit ogCU = null;
        JSONArray jsonWraps = wrapObj.getJSONArray("wraps");

        try {
            ogCU = parse(Paths.get(sampleFileName));
        } catch (IOException e) {
            System.out.println("WRAPPING FAILED Couldn't open file " + sampleFileName);
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            System.out.println("WRAPPING FAILED Couldn't parse " + sampleFileName);
            e.printStackTrace();
            return null;
        }

        Wrap[] wraps = new Wrap[jsonWraps.length()];
        for (int i = 0; i < jsonWraps.length(); i++) {
            JSONObject jsonWrap = jsonWraps.getJSONObject(i);

            // Create wrap object
            String wrapTypeStr = jsonWrap.getString("type");
            int lineNo = jsonWrap.getInt("lineNo");
            String exceptionType = jsonWrap.getString("exceptionType");

            WrapType wrapType;
            try {
                wrapType = WrapType.valueOf(wrapTypeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("WRAPPING FAILED Couldn't parse wrap type " + wrapTypeStr);
                e.printStackTrace();
                return null;
            }

            Wrap wrap = new Wrap(lineNo, exceptionType, wrapType);
            wraps[i] = wrap;
        } 

        Wraps allWraps = new Wraps(wraps);

        CompilationUnit cu = ogCU.clone();
        for (Wrap w : allWraps.getWraps()) {
            switch (w.getWrapType()) {
                case NPE:
                    wrapNPE(cu, w.getLineNo());
                    break;
                case AOB:
                    wrapArrayAccess(cu, w.getLineNo());
                    break;
                case CAST:
                    wrapCast(cu, w.getLineNo());
                    break;
                case CALL:
                    wrapCall(cu, w.getLineNo(), w.getExceptionTypes());
                    break;
                case NEGARRAY:
                    wrapNegArray(cu, w.getLineNo());
                    break;
                case DIVZERO:
                    wrapDivZero(cu, w.getLineNo());
                    break;
                default:
                    System.out.println("WRAPPING FAILED Unknown wrap type " + w.getWrapType());
                    return null;
            }
            //System.out.println(cu);
        }

        //TODO: we need to somehow assert that all of the wrapping has been done... 
        //In sample 2288 I think we have an issue (but it might be fixed by the trace parsing)

        if (cu.toString().equals(ogCU.toString())) {
            System.out.println("WRAPPING FAILED No changes made");
            return null;
        }
        System.out.println(cu);
        return cu.toString();
    }

    public static void main(String[] args) throws IOException {
        parser = new JavaParser();

        //TOOD: probably aDD THE project JARS HERE
        List<TypeSolver> jarTypeSolvers = new ArrayList<TypeSolver>();

        jarTypeSolvers.add(new ReflectionTypeSolver());
        TypeSolver solver = new CombinedTypeSolver(jarTypeSolvers);
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(solver);
        parser.getParserConfiguration().setSymbolResolver(symbolSolver);

        // The input should be two lists {wraptype, lineno}

        String sampleFileName = args[0];
        String wrapDataFilename = args[1];
        /*
         * String project_dir = args[2];
         * 
         * List<TypeSolver> jarTypeSolvers = new ArrayList<TypeSolver>();
         * jarTypeSolvers.add(new ReflectionTypeSolver());
         * 
         * 
         * String pathToWalk = project_dir;
         * try (Stream<Path> foos = Files.walk(Paths.get(pathToWalk))) {
         * foos.filter(Files::isRegularFile).filter(path ->
         * !path.toString().contains("Sample")).forEach(p -> {
         * if (p.toString().endsWith(".jar")) {
         * try {
         * //get the base name of the project_dir
         * String[] parts = project_dir.split("/");
         * String baseName = parts[parts.length - 1];
         * 
         * //split baseName by "_"
         * String[] arr = baseName.split("_");
         * String[] baseParts = Arrays.copyOfRange(arr, 1, arr.length);
         * //give a slice of 1: from baseParts
         * 
         * baseName = String.join("_", baseParts);
         * 
         * //check if p contains the base name
         * //get the basename of p
         * 
         * 
         * if (p.getFileName().toString().contains(baseName)) {
         * System.out.println("skipping " + p.toString());
         * return;
         * }
         * 
         * jarTypeSolvers.add(new JarTypeSolver(p));
         * } catch (IOException e) {
         * // TODO Auto-generated catch block
         * e.printStackTrace();
         * }
         * }
         * });
         * }
         * 
         * jarTypeSolvers.add(new JavaParserTypeSolver(project_dir));
         * 
         * 
         */

        InputStream is = new FileInputStream(wrapDataFilename);
        String jsonTxt = IOUtils.toString(is, "UTF-8");
        JSONObject wrapObj = new JSONObject(jsonTxt);
        wrap(wrapObj, sampleFileName);

    }
}
