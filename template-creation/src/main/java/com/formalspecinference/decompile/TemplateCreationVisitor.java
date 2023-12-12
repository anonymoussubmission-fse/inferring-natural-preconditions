package com.formalspecinference.decompile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hamcrest.core.IsInstanceOf;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.Node.PostOrderIterator;
import com.github.javaparser.ast.Node.TreeTraversal;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SuperExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.expr.AssignExpr.Operator;
import com.github.javaparser.ast.nodeTypes.NodeWithBody;
import com.github.javaparser.ast.nodeTypes.NodeWithCondition;
import com.github.javaparser.ast.nodeTypes.NodeWithStatements;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.type.PrimitiveType.Primitive;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedPrimitiveType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

import javassist.bytecode.analysis.ControlFlow.Block;

public class TemplateCreationVisitor extends ModifierVisitor<Void> {

    private JavaParser javaParser;
    private char counterName;
    private TypeSolver solver;

    TemplateCreationVisitor(JavaParser parser, TypeSolver tSolver) {
        super();

        javaParser = parser;
        counterName = 'a';
        solver = tSolver;
    }

    private char prevCounterName() {
        if (counterName == 'A') {
            return 'z';
        }
        return (char) (this.counterName - 1);
    }

    private void incrementCounterName() {
        if (counterName == 'z') {
            counterName = 'A';
        } else {
            counterName++;
        }
    }

    private void hoistAllCalls(List<Expression> exprs, Node md) {
        // Collections.reverse(exprs);

        exprs.removeIf(expr -> !(expr instanceof MethodCallExpr || expr instanceof ObjectCreationExpr));

        // hoist all MethodCallExpr and ObjectCreationExprs
        for (Expression expr : exprs) {
            Node parent = expr.getParentNode().get();

            Optional<NodeWithCondition> condNode = parent.findAncestor(NodeWithCondition.class);
                    //IT CANT BE IN A CONDITION! 
            if (!((condNode.isPresent() && ((NodeWithCondition) condNode.get()).getCondition().isAncestorOf(expr)))) {

                if ((parent instanceof ExpressionStmt) || (parent instanceof VariableDeclarator) ||
                        (parent instanceof AssignExpr) || (parent instanceof ThrowStmt)) {

                    Optional<Node> grandParent = parent.getParentNode();

                    
                    if (grandParent.isPresent() && !(grandParent.get() instanceof NodeWithBody)) {
                        continue;
                    } else if (!grandParent.isPresent()) {
                        continue;
                    }
                } 
            }

            if (Utils.DEBUG)
                System.out.println("Hoisting " + expr.toString());
            Statement localVar = extractToLocalVar(expr, getReturnType(expr));

            if (localVar != null) {
                addLocalDeclaration(localVar, expr, expr.findAncestor(Statement.class).get());
                char varChar = prevCounterName();
                String varName = "var_" + String.valueOf(varChar);

                NameExpr ident = new NameExpr(varName);

                parent = expr.getParentNode().get();
                parent.replace(expr, ident);
                ident.setParentNode(parent);

            }

            if (Utils.DEBUG) 
                System.out.println("CUrrent status of method " + md);

        }
    }

    public Visitable visit(MethodDeclaration md, Void arg) {

        if (Utils.DEBUG)
            System.out.println("=====================\n Processing " + md.getNameAsString());
        BlockStmt body = md.getBody().get();

        super.visit(body, arg);

        List<ReturnStmt> returns = body.findAll(ReturnStmt.class);
        for (ReturnStmt ret : returns ) {
            processReturn(ret);
        }

        List<ThrowStmt> _throws = body.findAll(ThrowStmt.class);
        for (ThrowStmt _throw : _throws ) {
            processThrows(_throw);
        }

        List<Expression> exprs = body.findAll(Expression.class, TreeTraversal.POSTORDER);
        // List<Expression> exprs = body.findAll(Expression.class);

        hoistAllCalls(exprs, md);

        // add a return false at the end
        body = md.getBody().get();
        int lastStmtIdx = body.getStatements().size() - 1;
        Boolean noReturn = lastStmtIdx >= 0 && !(body.getStatement(lastStmtIdx) instanceof ReturnStmt);

        if (noReturn || lastStmtIdx < 0) {
            body.addStatement(new ReturnStmt(new BooleanLiteralExpr(false)));
        }


        md.setPublic(true);
        md.setProtected(false);
        md.setPrivate(false);

        md.setType(boolean.class);
        md.setName("func");
        //md.setThrownExceptions(new NodeList<>());
        // md.getModifiers().replace(Modifier.protectedModifier(), Modifier.publicModifier());

        return md;
    }

    // oldStmt is the closest Statement ancestor to the expression we hoisted
    private Node addLocalDeclaration(Statement newHoistedDecl, Expression hoistedExpr, Statement hoistedExprParentStmt) {
        if (Utils.DEBUG) 
            System.out.println("The parent statement of the expression we are hoisting is " + hoistedExprParentStmt.getClass());
        System.out.flush();

        VariableDeclarator vde = newHoistedDecl.asExpressionStmt().getExpression().asVariableDeclarationExpr()
        .getVariable(0);
        VariableDeclarationExpr vdeNoInit = new VariableDeclarationExpr(vde.getType(), vde.getNameAsString());


        //Expression hoistedExpr = vde.getInitializer().get();
        try {
            NodeWithStatements nearestParentBlock = hoistedExprParentStmt.findAncestor(NodeWithStatements.class).get();
            Optional<NodeWithBody> nearestParentNoBlock = hoistedExprParentStmt.findAncestor(NodeWithBody.class);
        } catch (Exception e) {
            System.out.println("EXCEPTION");
            e.printStackTrace();
            //exit the process
            System.exit(1);

            //return vde;
        }



        NodeWithStatements nearestParentBlock = hoistedExprParentStmt.findAncestor(NodeWithStatements.class).get();
        Optional<NodeWithBody> nearestParentNoBlock = hoistedExprParentStmt.findAncestor(NodeWithBody.class);
        Optional<IfStmt> nearestParentIf = hoistedExprParentStmt.findAncestor(IfStmt.class);

        Optional<BlockStmt> blockToAddTo = Optional.empty();

        ArrayList<Node> ancestors = new ArrayList<Node>();
        ancestors.add((Node) nearestParentBlock);
        if (nearestParentNoBlock.isPresent()) {
            ancestors.add((Node) nearestParentNoBlock.get());
        }
        if (nearestParentIf.isPresent()) {
            ancestors.add((Node) nearestParentIf.get());
        }
        
        Node nearestAncestor = Utils.getNearest(ancestors, hoistedExprParentStmt);


        if (nearestAncestor.equals(nearestParentBlock)) {
            blockToAddTo = Optional.of((BlockStmt) nearestParentBlock);
        } else {
            // we need to add a block

            if (!(nearestAncestor instanceof IfStmt)) {
                    NodeWithBody nodeWithBody = (NodeWithBody) nearestAncestor;
                    BlockStmt newBody = new BlockStmt(new NodeList<Statement>(nodeWithBody.getBody()));
                    blockToAddTo = Optional.of(newBody);
                    nodeWithBody.setBody(newBody);
            }
        } 

        int idx = -1;

        if (blockToAddTo.isPresent()) {
            //Statement stmt : blockToAddTo.getStatements()
            for (int i=0; i<blockToAddTo.get().getStatements().size(); i++) {
                Statement stmt = blockToAddTo.get().getStatements().get(i);
                if (stmt.equals(hoistedExprParentStmt) || stmt.isAncestorOf(hoistedExprParentStmt)) {
                    idx = i;
                    break;
                }
            }

            if (idx == -1) throw new RuntimeException("Couldn't find the index of the hoisted expression's parent statement");
        }
        
       // blockToAddTo.getStatements().indexOf(hoistedExprParentStmt);


        // switch on all Statement types
        if (hoistedExprParentStmt instanceof AssertStmt) {
            blockToAddTo.get().addStatement(idx, newHoistedDecl);
        } else if (hoistedExprParentStmt instanceof BlockStmt) {
            throw new RuntimeException("BlockStmt shouldn't be the closest parent to a hoisted expression");
        } else if (hoistedExprParentStmt instanceof BreakStmt) {
            throw new RuntimeException("BreakStmt shouldn't be the closest parent to a hoisted expression");
        } else if (hoistedExprParentStmt instanceof ContinueStmt) {
            throw new RuntimeException("ContinueStmt shouldn't be the closest parent to a hoisted expression");
        } else if (hoistedExprParentStmt instanceof DoStmt) {

            throw new RuntimeException("We don't yet handle DoWhile");

            /* 
            Statement oldBody = ((DoStmt) hoistedExprParentStmt).getBody();
            NodeList<Statement> newBodyStmts = new NodeList<Statement>();
            if (oldBody instanceof BlockStmt) {
                newBodyStmts.addAll(((BlockStmt) oldBody).getStatements());
            } else {
                newBodyStmts.add(oldBody);
            }

            DoStmt myParent = (DoStmt) hoistedExprParentStmt;
            if (hoistedExpr.equals(myParent.getCondition()) || myParent.getCondition().isAncestorOf(hoistedExpr)) {
                //we need to hoist something in the condition 
                newBodyStmts.add(newHoistedDecl);
            } else {
                //hoisting something in the body
                newBodyStmts.add(idx, newHoistedDecl);
            }

            BlockStmt body = new BlockStmt(newBodyStmts);
            DoStmt newDo = new DoStmt(body, myParent.getCondition());

            blockToAddTo.addStatement(idx, vdeNoInit);
            blockToAddTo.replace(hoistedExprParentStmt, newDo);
            */
        } else if (hoistedExprParentStmt instanceof EmptyStmt) {
            throw new RuntimeException("EmptyStmt shouldn't be the closest parent to a hoisted expression");
        } else if (hoistedExprParentStmt instanceof ExplicitConstructorInvocationStmt) {
            throw new RuntimeException("Super calls shouldn't be the closest parent to a hoisted expression");
        } else if (hoistedExprParentStmt instanceof ExpressionStmt) {

            if (!blockToAddTo.isPresent()) {
                //if its in the else, also just add a block stmt
                IfStmt gParent = ((IfStmt) nearestAncestor);
                BlockStmt newBlockStmt = new BlockStmt(new NodeList<Statement>(newHoistedDecl));

                Optional<Statement> elseStmt = gParent.getElseStmt();
                if (elseStmt.isPresent() && elseStmt.get().isAncestorOf(hoistedExpr)) {
                    newBlockStmt.getStatements().add(gParent.getElseStmt().get());
                    gParent.setElseStmt(newBlockStmt);
                } else { 
                    newBlockStmt.getStatements().add(gParent.getThenStmt());
                    gParent.setThenStmt(newBlockStmt);
                }

                return newBlockStmt;
            } else {
                blockToAddTo.get().addStatement(idx, newHoistedDecl);
                newHoistedDecl.setParentNode(blockToAddTo.get());
                hoistedExprParentStmt.setParentNode(blockToAddTo.get());
            }

        } else if (hoistedExprParentStmt instanceof ForEachStmt) {
            //if the expression is the iterable, just hoist before the loop
            ForEachStmt ogForEachStmt = (ForEachStmt) hoistedExprParentStmt;
            if (hoistedExpr.equals(ogForEachStmt.getIterable()) ||  ogForEachStmt.getIterable().isAncestorOf(hoistedExpr)) {
                blockToAddTo.get().addStatement(idx, newHoistedDecl);
            } else {
                //if the expression is the body, add it there
                BlockStmt body = new BlockStmt(new NodeList<Statement>(ogForEachStmt.getBody()));
                body.addStatement(newHoistedDecl);
                ogForEachStmt.setBody(body);
            }

        } else if (hoistedExprParentStmt instanceof ForStmt) {
            //if the expression is the init, hoist it above the loop.
            //if the expression is the comparison, it needs to be evaluated each time.
            ForStmt ogForStmt = (ForStmt) hoistedExprParentStmt;
            Expression ogComp = ogForStmt.getCompare().get();
            Expression ogInit = ogForStmt.getInitialization().get(0);
            Expression ogUpdate = ogForStmt.getUpdate().get(0);
            if (hoistedExpr.equals(ogInit) || ogInit.isAncestorOf(hoistedExpr)) {
                if (Utils.DEBUG)
                    System.out.println("hoisting init");
                blockToAddTo.get().addStatement(idx, newHoistedDecl);

            } else if (hoistedExpr.equals(ogComp) || ogComp.isAncestorOf(hoistedExpr)) {
                if (Utils.DEBUG)
                    System.out.println("hoisting compare");

                //insert a new check for conditional
                //put ogComp in parenthesis
                

                Expression cond = new UnaryExpr(new EnclosedExpr(ogComp), UnaryExpr.Operator.LOGICAL_COMPLEMENT);
                IfStmt newCondition = new IfStmt(cond, new BreakStmt(), null);


                NodeList<Statement> newStmts = new NodeList<Statement>();
                newStmts.add(newHoistedDecl);
                newStmts.add(newCondition);
                //newStmts.add(newHoistedDecl);
                if (ogForStmt.getBody() instanceof BlockStmt) {
                    newStmts.addAll(((BlockStmt) ogForStmt.getBody()).getStatements());
                } else {
                    newStmts.add(ogForStmt.getBody());
                }

                BlockStmt newBody = new BlockStmt(newStmts);

                ForStmt newFor = new ForStmt(ogForStmt.getInitialization(), new BooleanLiteralExpr(true), ogForStmt.getUpdate(), newBody);
                blockToAddTo.get().replace(ogForStmt, newFor);

            } else if (hoistedExpr.equals(ogUpdate) || ogUpdate.isAncestorOf(hoistedExpr)) {
                if (Utils.DEBUG)
                    System.out.println("hoisting update");

                String varName = String.valueOf("_var_is_first_itr");
                VariableDeclarator vd_update = new VariableDeclarator(PrimitiveType.booleanType(), varName, new BooleanLiteralExpr(false));
                ExpressionStmt vdeUpdate = new ExpressionStmt(new VariableDeclarationExpr(vd_update));

                BlockStmt thenStmt = new BlockStmt(new NodeList<Statement>(new ExpressionStmt(new AssignExpr(new NameExpr(varName), new BooleanLiteralExpr(true), Operator.ASSIGN))));
                BlockStmt elseStmt = new BlockStmt(new NodeList<Statement>(newHoistedDecl, new ExpressionStmt(ogUpdate)));

                IfStmt updateIfStmt = new IfStmt(new UnaryExpr(new NameExpr(varName), UnaryExpr.Operator.LOGICAL_COMPLEMENT), thenStmt, elseStmt);

                Expression cond = new UnaryExpr(new EnclosedExpr(ogComp), UnaryExpr.Operator.LOGICAL_COMPLEMENT);
                IfStmt newCondition = new IfStmt(cond, new BreakStmt(), null);

                //int lastStmt = blockToAddTo.getStatements().size() - 1;

                blockToAddTo.get().addStatement(idx, vdeUpdate);
                NodeList<Statement> newStmts = new NodeList<Statement>(updateIfStmt, newCondition);

                if (ogForStmt.getBody() instanceof BlockStmt) {
                    newStmts.addAll(((BlockStmt) ogForStmt.getBody()).getStatements());
                } else {
                    newStmts.add(ogForStmt.getBody());
                }

                BlockStmt newBody = new BlockStmt(newStmts);
                ForStmt newFor = new ForStmt(ogForStmt.getInitialization(), ogForStmt.getCompare().get(), new NodeList<Expression>(), newBody);

                blockToAddTo.get().replace(ogForStmt, newFor);
               // hoistedExprParentStmt.asForStmt().getBody().addStatement(lastStmt, newHoistedDecl);

            } else {
                //expression is in the body
                if (Utils.DEBUG) 
                    System.out.println("hoisting body");
                blockToAddTo.get().addStatement(idx, newHoistedDecl);
            }
        } else if (hoistedExprParentStmt instanceof IfStmt) {
            IfStmt ogIfStmt = (IfStmt) hoistedExprParentStmt;

            //first just add blocks
            if (!(ogIfStmt.getThenStmt() instanceof BlockStmt)) {
                Statement thenStmt = ogIfStmt.getThenStmt();
                BlockStmt thenBlock = new BlockStmt(new NodeList<Statement>(thenStmt));
                
                //ogIfStmt.getThenStmt().setParentNode(thenBlock);
                ogIfStmt.setThenStmt(thenBlock);
                thenStmt.setParentNode(thenBlock);

            }
            
            if (ogIfStmt.getElseStmt().isPresent() && !(ogIfStmt.getElseStmt().get() instanceof BlockStmt)) {
                Statement elseStmt = ogIfStmt.getElseStmt().get();
                NodeList<Statement> stmts = new NodeList<Statement>(elseStmt);
                BlockStmt elseBlock = new BlockStmt(stmts);
                //stmts.setParentNode(elseBlock);
                ogIfStmt.setElseStmt(elseBlock);
                elseStmt.setParentNode(elseBlock);

            }

            //If the expression is in an else if condition, we need to turn it into a nested if..
            if (Utils.isElseIf(ogIfStmt, hoistedExpr)) {
                 //add the hoisted declaration as the last stmt of the blockToAddTo body
                //cond is the hoisted var name
                //body stays the same
                //assert (false);
                assert (!blockToAddTo.isPresent());

                IfStmt elseStmt = ogIfStmt;

                Statement ogElse = elseStmt.getElseStmt().isPresent() ? elseStmt.getElseStmt().get() : null;
                //Statement ogElse = elseStmt.getElseStmt().get();

                IfStmt newIfStmt = new IfStmt(elseStmt.getCondition(), elseStmt.getThenStmt(), ogElse);
                //elseStmt.setParentNode(newIfStmt);

                NodeList<Statement> newStmts = new NodeList<Statement>();
                newStmts.add(newHoistedDecl);
                newStmts.add(newIfStmt);

                //newStmts.addAll(((BlockStmt) elseStmt.getThenStmt()).getStatements());
                IfStmt parent = (IfStmt) ogIfStmt.getParentNode().get();
                BlockStmt newElseStmt = new BlockStmt(newStmts);
                newIfStmt.setParentNode(newElseStmt);

                parent.setElseStmt(newElseStmt);

                return (Node) newIfStmt;


            } else if (hoistedExpr.equals(ogIfStmt.getCondition()) || ogIfStmt.getCondition().isAncestorOf(hoistedExpr)) {
                //If the expression is in the condition, hoist it above 
                if (Utils.DEBUG)
                    System.out.println("Hoisted expr is in the condition");
                blockToAddTo.get().addStatement(idx, newHoistedDecl);


            } else {
                //if the expression is in the body, add it there
                NodeList<Statement> newStmts = new NodeList<Statement>();
                newStmts.add(newHoistedDecl);
                //if (ogIfStmt.getThenStmt() instanceof BlockStmt) {
                newStmts.addAll(((BlockStmt) ogIfStmt.getThenStmt()).getStatements());
                //} else {
                //    newStmts.add(ogIfStmt.getThenStmt());
                //}

                ogIfStmt.setThenStmt(new BlockStmt(newStmts));
            }

        } else if (hoistedExprParentStmt instanceof LabeledStmt) {
            throw new RuntimeException("Don't yet handle LabeledStmts");
        } else if (hoistedExprParentStmt instanceof LocalClassDeclarationStmt) {
            throw new RuntimeException("Don't yet handle LocalCalssDeclarationStmts");
        } else if (hoistedExprParentStmt instanceof LocalRecordDeclarationStmt) {
            throw new RuntimeException("Don't yet handle LocalRecordDeclarationStmts");
        } else if (hoistedExprParentStmt instanceof ReturnStmt) {
            if (!blockToAddTo.isPresent()) {
                IfStmt gParent = ((IfStmt) nearestAncestor);
                Statement thenStmt = gParent.getThenStmt();
                BlockStmt newBlockStmt = new BlockStmt(new NodeList<Statement>(newHoistedDecl, thenStmt));
                gParent.setThenStmt(newBlockStmt);
                newHoistedDecl.setParentNode(newBlockStmt);
                thenStmt.setParentNode(newBlockStmt);

                return (Node) newBlockStmt;
            } else {
                blockToAddTo.get().addStatement(idx, newHoistedDecl);
            }

        } else if (hoistedExprParentStmt instanceof SwitchStmt) {
            throw new RuntimeException("I DON'T KNOW HOW TO HANDLE SWITCH STATEMENTS YET!");
        } else if (hoistedExprParentStmt instanceof SynchronizedStmt) {
            throw new RuntimeException("We don't handle SynchronizedStmts");
        } else if (hoistedExprParentStmt instanceof ThrowStmt) {
            throw new RuntimeException("ThrowStmt shouldn't be the closest parent to a hoisted expression");
        } else if (hoistedExprParentStmt instanceof TryStmt) {
            throw new RuntimeException("TryStmt shouldn't be the closest parent to a hoisted expression.. It should be a block");
        } else if (hoistedExprParentStmt instanceof UnparsableStmt) {
            throw new RuntimeException("UnparsableStmt shouldn't be the closest parent to a hoisted expression");
        } else if (hoistedExprParentStmt instanceof WhileStmt) {
            //if the hoisted expression is in the condition, hoist it as the first stateents in the while loop
            //  break if false

            if (hoistedExpr.equals(((WhileStmt) hoistedExprParentStmt).getCondition()) ||
                    ((WhileStmt) hoistedExprParentStmt).getCondition().isAncestorOf(hoistedExpr)) {

                Expression oldCondition = ((WhileStmt) hoistedExprParentStmt).getCondition();
                //wrap oldCondition in parens

                
                Expression newCondition = new UnaryExpr(new EnclosedExpr(oldCondition),
                    UnaryExpr.Operator.LOGICAL_COMPLEMENT);
    
                IfStmt newIfStmt = new IfStmt(newCondition, new BlockStmt(new NodeList<Statement>(new BreakStmt())), null);

                Statement newBody = ((WhileStmt) hoistedExprParentStmt).getBody();


                NodeList<Statement> newBodyStmts = new NodeList<Statement>(newHoistedDecl, newIfStmt);
                if (newBody instanceof BlockStmt) {
                    newBodyStmts.addAll(((BlockStmt) newBody).getStatements());
                } else {
                    newBodyStmts.add(newBody);
                }
    
                WhileStmt newLoop = new WhileStmt(new BooleanLiteralExpr(true),
                        new BlockStmt(newBodyStmts));
    
                //blockToAddTo.addStatement(idx, vdeNoInit);
                blockToAddTo.get().replace(hoistedExprParentStmt, newLoop);

            } else {
                //if the hoisted expression is in the body, then just add it to the body
                blockToAddTo.get().addStatement(idx, newHoistedDecl);
            }
        } else if (hoistedExprParentStmt instanceof YieldStmt) {
            //blockToAddTo.addStatement(idx, newHoistedDecl);
            throw new RuntimeException("Don't yet handle YieldStmts");
        } else {
            throw new RuntimeException("ERROR: Unhandled Statement type " + hoistedExprParentStmt.getClass().getName());
        }
        //System.out.println("after hoist");
        //System.out.println(blockToAddTo);
        return (Node) blockToAddTo.get();
        //return;

        /*
        NodeList<Statement> statements = ancestorToAddTo.getStatements();

        
        for (Statement statement : statements) {
            if (statement.isAncestorOf(hoistedExprParentStmt) || statement.equals(hoistedExprParentStmt)) {
                statements.addBefore(newHoistedDecl, statement);
                newHoistedDecl.setParentNode((Node) ancestorToAddTo);
                return (Node) ancestorToAddTo;
            }
        }
        */
    }

    public MethodDeclaration processConstructor(ConstructorDeclaration md) {
        BlockStmt body = md.getBody();

        List<ExplicitConstructorInvocationStmt> supers = body.findAll(ExplicitConstructorInvocationStmt.class);

        if (Utils.DEBUG) {
            System.out.println("Processing constructor " + md.getBody());
            System.out.println("Supers: " + supers.size());
        }

        if (supers.size() > 0) {
            throw new RuntimeException("Can't handle super calls in constructors yet");
        }

        List<ThrowStmt> _throws = body.findAll(ThrowStmt.class);
        for (ThrowStmt _throw : _throws ) {
            processThrows(_throw);
        }

        // List<Expression> exprs = body.findAll(Expression.class);
        List<Expression> exprs = body.findAll(Expression.class, TreeTraversal.POSTORDER);

        hoistAllCalls(exprs, md);
        super.visit(body, null);

        int lastStmt = md.getBody().getStatements().size() - 1;
        if (lastStmt >= 0) {
            body.addStatement(new ReturnStmt(new BooleanLiteralExpr(false)));
        }

        MethodDeclaration newMD = new MethodDeclaration(md.getModifiers(), "func", PrimitiveType.booleanType(),
                md.getParameters());
        newMD.setThrownExceptions(new NodeList<>());
        newMD.setBody(body);
        newMD.setPublic(true);
        newMD.setProtected(false);
        newMD.setPrivate(false);

        return newMD;
    }

    public ReturnStmt processThrows(ThrowStmt t) {
        t.getParentNode().get().replace(t, new ReturnStmt(new BooleanLiteralExpr(true)));
        return new ReturnStmt(new BooleanLiteralExpr(true));
    }

    public ReturnStmt processReturn(ReturnStmt r) {
        if (!r.getExpression().isPresent()) {
            r.setExpression(new BooleanLiteralExpr(false));
            return r;
        }

        Optional<MethodDeclaration> parentMethod = r.findAncestor(MethodDeclaration.class);

        if (!parentMethod.isPresent()) {
            return r;
        }

        Type methodType = parentMethod.get().getType();

        Expression expr = r.getExpression().get();
        
        Statement localVar = extractToLocalVar(expr, methodType);

        if (localVar != null) {
            addLocalDeclaration(localVar, expr, expr.findAncestor(Statement.class).get());
        }

        r.setExpression(new BooleanLiteralExpr(false));
        return r;
    }

    /* type utils */
    Type getRefType(ResolvedType resolved) {
        if (resolved.isPrimitive()) {
            ResolvedPrimitiveType asPrimitive = resolved.asPrimitive();
            switch (asPrimitive.describe().toLowerCase()) {
                case "byte":
                    return new PrimitiveType(Primitive.BYTE);
                case "short":
                    return new PrimitiveType(Primitive.SHORT);
                case "char":
                    return new PrimitiveType(Primitive.CHAR);
                case "int":
                    return new PrimitiveType(Primitive.INT);
                case "long":
                    return new PrimitiveType(Primitive.LONG);
                case "boolean":
                    return new PrimitiveType(Primitive.BOOLEAN);
                case "float":
                    return new PrimitiveType(Primitive.FLOAT);
                case "double":
                    return new PrimitiveType(Primitive.DOUBLE);
            }
        }

        String myResolved = resolved.describe();
        if (myResolved.startsWith("com.formalspecinference.decompile.TestClasses.")) {
            myResolved = myResolved.substring(myResolved.lastIndexOf(".")+1);
        }

        return javaParser.parseType(myResolved).getResult().get();
    }

    private Type getReturnType(Expression expr) {
        ResolvedType returnTypeResolved = null;
        if (expr instanceof MethodCallExpr) {
            try {
                returnTypeResolved = expr.asMethodCallExpr().calculateResolvedType();
            } catch (UnsolvedSymbolException | UnsupportedOperationException e) {
                //e.printStackTrace();
                return javaParser.parseType("java.lang.Object").getResult().get();
            }
        } else if (expr instanceof ObjectCreationExpr) {
            ObjectCreationExpr oc = (ObjectCreationExpr) expr;
            ClassOrInterfaceType t = oc.getType();
            returnTypeResolved = t.resolve().asReferenceType();
        }

        return getRefType(returnTypeResolved);

    }

    private Statement extractToLocalVar(Expression expr, Type t) {
        if (t instanceof VoidType) {
            return null;
        }

        String varName = String.valueOf("var_" + this.counterName);
        incrementCounterName();
        VariableDeclarator vd = new VariableDeclarator(t, varName, expr.clone());
        VariableDeclarationExpr vde = new VariableDeclarationExpr(vd);
        return new ExpressionStmt(vde);
    }
}
