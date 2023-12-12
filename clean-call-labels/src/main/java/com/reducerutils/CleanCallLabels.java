package com.reducerutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.commons.io.IOUtils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.printer.YamlPrinter;

public class CleanCallLabels {
    public static JavaParser parser;
    static int count = 0;

    public static MethodDeclaration parse(Path p) throws IOException, ParseException {

        //open file p and read from it
        InputStream is = new FileInputStream(p.toFile());
        String fileContents = IOUtils.toString(is, "UTF-8");

        ParseResult<MethodDeclaration> result = parser.parseMethodDeclaration(fileContents);
        if (!result.isSuccessful()) {
            System.out.println(result.getProblems());
            throw new ParseException();
        }
        Optional<MethodDeclaration> optCu = result.getResult();
        MethodDeclaration cu = optCu.get();
        return cu;
    }

   
    public static void visitVars(MethodDeclaration cu, String filename) {
        LabelCleanVisitor visitor = new LabelCleanVisitor();
        cu.accept(visitor, null);

        Map<String, Integer> uses = visitor.getUses();
        if (uses.keySet().size() > 0 && uses.values().stream().anyMatch(x -> x == 0)) {
            count += 1;

            //System.out.println(cu.toString());

            for (String use : uses.keySet()) {
                if (uses.get(use) > 0) continue;
                //System.out.println("removing..." + use);

                RemoveVarVisitor removeVarVisitor = new RemoveVarVisitor(use);
                cu.accept(removeVarVisitor, null);
            }

            //System.out.println(cu.toString());

            //Write cu.toString() to file filename
            try {
                File file = new File(filename);
                System.out.println("writing to" + file.toString());
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(cu.toString().getBytes());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws IOException {
        parser = new JavaParser();
        MethodDeclaration cu = null;

        File dir = new File("precondition-data-collection/all-clean-data/");
        File[] directoryListing = dir.listFiles();
        for (File proj : directoryListing) {
            if (!proj.isDirectory()) continue;
            for (File child : proj.listFiles()) {
               
                if (!child.toString().contains("Label")) continue;

                //System.out.println(child.toString());


                try {
                    cu = parse(child.toPath());
                } catch (IOException e) {
                    System.out.println("WRAPPING FAILED Couldn't open file " + child.toString());
                    e.printStackTrace();
                    return;
                } catch (ParseException e) {
                    System.out.println("WRAPPING FAILED Couldn't parse " + child.toString());
                    e.printStackTrace();
                    return;
                }

                visitVars(cu, child.toString());

                //write to the original file with the new cu.toString()



                //System.out.println();
            }
        }
        System.out.println("count: " + count);
    }

}
