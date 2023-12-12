package com.formalspecinference.decompile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.simple.JSONObject;

public class Writer {

    //write the metadata file to the metadata path + project name + Sample{Number}_method.json
    public static void writeMeta(String methodSignature, Path prefix, Path projectPath, Path classPath, String sampleName) throws IOException {

        Path globalMetaDir = prefix.resolve("metadata");
        Path metadir = globalMetaDir.resolve(projectPath.getFileName());

        //remove projectPath from classPath 
        String originalClassName = classPath.toString().substring(projectPath.toString().length() + 1);

        
        JSONObject obj = new JSONObject();
        obj.put("Class", originalClassName.replace("/", "."));
        obj.put("Method", methodSignature);
        obj.put("Coverage", null);
        obj.put("Args", null);

        File globalDirectory = globalMetaDir.toFile();
        if (!globalDirectory.exists()) {
            globalDirectory.mkdir();
        }

        File directory = metadir.toFile();
        if (!directory.exists()) {
            directory.mkdir();
        }

        Path metaFile = metadir.resolve(sampleName + ".json");

        Files.write(metaFile, obj.toString().getBytes());
    }

    public static void writeSample(String content, String outFile) throws IOException {
        PrintWriter writer = new PrintWriter(outFile, "UTF-8");
        writer.write(content);
        writer.close();
    }

    public static void writeError(String content, String outFile) throws IOException {
        //append content to outFile
        PrintWriter writer = new PrintWriter(new FileOutputStream(new File(outFile), true));
        writer.append("\n==================================\n" + content);
        writer.close();
    }

}
