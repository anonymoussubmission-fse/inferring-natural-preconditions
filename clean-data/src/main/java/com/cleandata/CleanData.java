package com.cleandata;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.PKIXRevocationChecker.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.serialization.JavaParserJsonSerializer;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class CleanData {
    public static JavaParser parser;
    public static JavaParserJsonSerializer serializer;

    public static CompilationUnit parse(Path p) throws IOException, ParseException {
        ParseResult<CompilationUnit> result = parser.parse(p);
        if (!result.isSuccessful()) {
            throw new ParseException();
        }
        Optional<CompilationUnit> optCu = result.getResult();
        CompilationUnit cu = optCu.get();
        return cu;
    }


    private static void writeSample(String content, String outFile) throws IOException {
        PrintWriter writer = new PrintWriter(outFile, "UTF-8");
        writer.write(content);
        writer.close();
    }

    private static MethodDeclaration getMethod(CompilationUnit preconditionCU, boolean precondition) {
        List<MethodDeclaration> methods = preconditionCU.findAll(MethodDeclaration.class);
        if (methods.size() != 1) {
            throw new RuntimeException("Only one method is expected");
        }

        MethodDeclaration method = methods.get(0);

        if (precondition && !method.getNameAsString().equals("func")) {
            throw new RuntimeException("Method func is expected");
        }
        
        return method;
    }

    public static void main(String[] args) throws IOException {
        parser = new JavaParser();
        String sampleFileName = args[0];

        String outFile = sampleFileName.replace("all-data", "all-clean-data").replace("_method_reduced", "").replace("_method", "").replace("/data","").replace("Sample", "Label");
        CompilationUnit sampleCU;

        try {
            sampleCU = parse(Paths.get(sampleFileName));
        } catch (IOException e) {
            System.out.println("CHECK FAILED Couldn't open file " + sampleFileName);
            e.printStackTrace();
            return;
        } catch (ParseException e) {
            System.out.println("CHECK FAILED Couldn't parse " + sampleFileName);
            e.printStackTrace();
            return;
        }

        MethodDeclaration sample = getMethod(sampleCU, false);

        File directory = new File(outFile).getParentFile();
        System.out.println(outFile);

        if (!directory.exists()){
            System.out.println("Making " + directory.getName());
            directory.mkdir();
        }

        writeSample(sample.toString(), outFile);        
    }
}
