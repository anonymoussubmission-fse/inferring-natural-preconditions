package com.formalspecinference.decompile;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import org.apache.commons.lang3.concurrent.CallableBackgroundInitializer;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

public class CreateTemplate {
    public static JavaParser parser;
    public static int validMethods = 0;
    public static int invalidMethods = 0;
   // public static int sampleNum;

    public static String assembleSample(String packageName, String imports, String sampleName, String ogClassName,
            String constructors, String method) {
        return "package " + packageName + ";\n" + imports + " class " + sampleName + " extends " + ogClassName + " { \n"
                + constructors + "\n" + method + " \n}";
    }

    public static Map<String, Integer> generateSample(CompilationUnit cu, Path prefix, Path projectPath, Path classPath, String packageName, JavaParser parser,
            TypeSolver solver, Integer sampleNum) throws IOException, ClassNotFoundException {

        Integer broken = 0;
        String ogClassName = classPath.getFileName().toString().replace(".java", "");
        
       // List<MethodDeclaration> mds = cu.findAll(MethodDeclaration.class);
        List<ConstructorDeclaration> cus = cu.findAll(ConstructorDeclaration.class);
        List<CallableDeclaration> mds = cu.findAll(CallableDeclaration.class);


        //change all private fields to protected so that we can accesss them in the template
        CompilationUnit ogCu = cu.clone();
        PrivateVisitor accessVisitor = new PrivateVisitor();

        try {
            ogCu.accept(accessVisitor, null);
        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException("Error in accessVisitor");
        }

       String imports = Utils.getImports(cu);

        for(CallableDeclaration md : mds) {

            String constructors = Utils.getConstructorText(cus, ogClassName, sampleNum);

            TemplateCreationVisitor visitor = new TemplateCreationVisitor(parser, solver);
            String sampleName = "Sample" + String.valueOf(sampleNum) + "_method";

            CallableDeclaration ogMd = md.clone();

         
            if (md instanceof MethodDeclaration && !((MethodDeclaration) md).getBody().isPresent()) continue;

            List<CallableDeclaration> declChildren = md.findAll(CallableDeclaration.class);


            //if it has a method declaration in the body, continue 
            declChildren.removeIf(x -> ogMd.equals(x));
            if (declChildren.size() > 0) continue;

            //if its a child of a method declaration, contineu
            if(md.findAncestor(CallableDeclaration.class).isPresent()) continue;

            try {
                if (md instanceof ConstructorDeclaration) {
                    md = visitor.processConstructor((ConstructorDeclaration) md);
                } else {                
                    md.accept(visitor, null);
                }
                
            } catch (Exception e) {
                if (Utils.DEBUG) {
                    e.printStackTrace();
                }
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                Writer.writeError(ogMd.getDeclarationAsString() + "\n" + sw.toString(), "log.txt");
                broken += 1;
                continue;                   
            }    

            //System.out.println("AFTER VISITATION " + md.toString());

            cu.getTypes().get(0).getMembers().add(ogMd);
            
            String template = md.toString();

            Writer.writeMeta(ogMd.getSignature().asString(), prefix, projectPath, classPath, sampleName);

            String classTxt = assembleSample(packageName, imports, sampleName, ogClassName, constructors, template);

            String outFile = classPath.toString().replace(classPath.getFileName().toString(), sampleName + ".java");
            Writer.writeSample(classTxt, outFile);

            sampleNum++;
        }

        //TODO: i have no idea what this is....
        int numEq = 0;
        for (MethodDeclaration chNode : ogCu.findAll(MethodDeclaration.class)) {
            if (chNode.getName().toString().equals("func")) {
                numEq++;
            }
        }
        for (ConstructorDeclaration chNode : ogCu.findAll(ConstructorDeclaration.class)) {
            if (chNode.getName().toString().equals("func")) {
                numEq++;
            }
        }
        assert numEq == 1;

        //write the original class file - it's new now because of the access changes
        Writer.writeSample(ogCu.toString(), classPath.toString());

        Map<String, Integer> out =  new HashMap<String, Integer>();

        out.put("broken", broken);
        out.put("sampleNum", sampleNum);
        return out;
    }

    public static void main(String[] argv) throws Exception {
        parser = new JavaParser();


        List<TypeSolver> jarTypeSolvers = new ArrayList<TypeSolver>();
        jarTypeSolvers.add(new ReflectionTypeSolver());

        if (argv.length != 4) {
            System.out.println("Usage: <prefix> <project> <classPath> <sampleNum>");
            System.out.println("Example: projects/ 72_battlecry bcry/battlecryGUI.java 19853");

            System.exit(1);
        }


        Path prefix = Paths.get(argv[0]);
        Path project = Paths.get(argv[1]);
        Integer sampleNum = Integer.parseInt(argv[3]);

        Path classPath = prefix.resolve(project).resolve(Paths.get(argv[2]));

        //join two prefix and project into one path
        Path projectPath = prefix.resolve(project);

        try (Stream<Path> project_files = Files.walk(projectPath)) {
            project_files.filter(Files::isRegularFile).filter(f -> !f.toString().contains("Sample")).forEach(f -> {
                if (f.toString().endsWith(".jar")) {
                    try {
                        //System.out.println("adding jar " + f.toString());
                        jarTypeSolvers.add(new JarTypeSolver(f));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        CombinedTypeSolver solver = new CombinedTypeSolver(jarTypeSolvers);

        
        try {
            CompilationUnit cu = Utils.parse(parser, solver, classPath);
            ClassOrInterfaceDeclaration cls = cu.findFirst(ClassOrInterfaceDeclaration.class).get();
            if (cls.isAbstract() || cls.isFinal()) {
                for (int i=0; i<cls.getMethods().size(); i++) {
                    Writer.writeError(cls.getNameAsString() + "\n is either abstract or final", "log.txt");

                }
                System.out.println(cls.getMethods().size() + "," + sampleNum);
                return;
            }

            String _package =  Utils.getPackage(cu);
            
            Map<String, Integer> out = generateSample(cu, prefix, projectPath, classPath, _package, parser, solver, sampleNum);
            System.out.println(out.get("broken") + "," + out.get("sampleNum"));


        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);

        } catch (ParseException e) {
            System.out.println(1 + "," + sampleNum);
        } catch (NoSuchElementException e) {
            System.out.println(1 + "," + sampleNum);
        }

        //System.out.println(sampleNum);
                
    }
}
