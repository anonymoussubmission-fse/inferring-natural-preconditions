package com.stats;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.commons.io.IOUtils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.printer.YamlPrinter;

public class MethodStats {
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

    public static void main(String[] args) throws IOException {
        parser = new JavaParser();
        CompilationUnit ogCU = null;

        String classFileName = args[0];

        try {
            ogCU = parse(Paths.get(classFileName));
            List<MethodDeclaration> mds = ogCU.findAll(MethodDeclaration.class);
            List<ConstructorDeclaration> cus = ogCU.findAll(ConstructorDeclaration.class);
            System.out.println(mds.size() + cus.size());
        } catch (IOException e) {
            System.out.println("WRAPPING FAILED Couldn't open file " + classFileName);
            e.printStackTrace();
            return;
        } catch (ParseException e) {
            System.out.println("WRAPPING FAILED Couldn't parse " + classFileName);
            e.printStackTrace();
            return;
        }

        // YamlPrinter printer = new YamlPrinter(true);
        // System.out.println(printer.output(ogCU));

    }
}
