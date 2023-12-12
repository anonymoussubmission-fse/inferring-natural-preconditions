package com.reducerutils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.expr.AssignExpr.Operator;
import com.github.javaparser.ast.nodeTypes.NodeWithBody;
import com.github.javaparser.ast.nodeTypes.NodeWithStatements;
import com.github.javaparser.ast.stmt.AssertStmt;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.LabeledStmt;
import com.github.javaparser.ast.stmt.LocalClassDeclarationStmt;
import com.github.javaparser.ast.stmt.LocalRecordDeclarationStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.SynchronizedStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.UnparsableStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.stmt.YieldStmt;
import com.github.javaparser.ast.type.PrimitiveType;

import javassist.bytecode.analysis.ControlFlow.Block;

public class Utils {

    public static Node getNearest(List<Node> ancestors) {
        if (ancestors.size() == 1)
            return ancestors.get(0);

        Node nearestAncestor = ancestors.get(0);
        for (Node ancestor : ancestors) {
            if (nearestAncestor.isAncestorOf(ancestor)) {
                nearestAncestor = ancestor;
            }
        }

        return nearestAncestor;

    }

    public static class BlocksToAddTo {
        public final Optional<BlockStmt> blockToAddTo;
        public final Node nearestAncestor;

        public BlocksToAddTo(Optional<BlockStmt> blockToAddTo, Node nearestAncestor) {
            this.blockToAddTo = blockToAddTo;
            this.nearestAncestor = nearestAncestor;
        }
    }

    public static BlocksToAddTo getBlockToAddTo(Statement exceptionalExprParentStmt) {

        try {
            NodeWithStatements nearestParentBlock = exceptionalExprParentStmt.findAncestor(NodeWithStatements.class).get();
            Optional<NodeWithBody> nearestParentNoBlock = exceptionalExprParentStmt.findAncestor(NodeWithBody.class);
        } catch (Exception e) {
            System.out.println("EXCEPTION");
            e.printStackTrace();
            // exit the process
            System.exit(1);
        }

        NodeWithStatements nearestParentBlock = exceptionalExprParentStmt.findAncestor(NodeWithStatements.class).get();
        Optional<NodeWithBody> nearestParentNoBlock = exceptionalExprParentStmt.findAncestor(NodeWithBody.class);
        Optional<IfStmt> nearestParentIf = exceptionalExprParentStmt.findAncestor(IfStmt.class);

        Optional<BlockStmt> blockToAddTo = Optional.empty();

        ArrayList<Node> ancestors = new ArrayList<Node>();
        ancestors.add((Node) nearestParentBlock);
        if (nearestParentNoBlock.isPresent()) {
            ancestors.add((Node) nearestParentNoBlock.get());
        }
        if (nearestParentIf.isPresent()) {
            ancestors.add((Node) nearestParentIf.get());
        }

        Node nearestAncestor = Utils.getNearest(ancestors);

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

        return new Utils.BlocksToAddTo(blockToAddTo, nearestAncestor);
    }

    public static boolean isStaticAccess(Expression scope) {
        String rs;
        try {
            rs = scope.calculateResolvedType().describe();
        } catch (Exception e) {
            return false;
        }

        rs = rs.substring(rs.lastIndexOf(".") + 1);

        return scope.isNameExpr() && scope.asNameExpr().getNameAsString().equals(rs);
    }

    public static boolean isElseIf(IfStmt parentOfExprToHoist, Node exprToHoist) {
        Node parent = parentOfExprToHoist.getParentNode().get();
        if (!(parent instanceof IfStmt)) {
            return false;
        }

        Optional<Statement> elseStmt = ((IfStmt) parent).getElseStmt();
        if (!elseStmt.isPresent()) {
            return false;
        }

        return (elseStmt.get() instanceof IfStmt) && elseStmt.get().asIfStmt().getCondition().isAncestorOf(exprToHoist);
    }

    public static int getIndexToInsertAt(BlockStmt block, Statement nullableExprParent) {
        if (block == null)
            return -1;
        int idx = -1;
        for (int i = 0; i < block.getStatements().size(); i++) {
            Statement stmt = block.getStatements().get(i);
            if (stmt.equals(nullableExprParent) || stmt.isAncestorOf(nullableExprParent)) {
                idx = i;
                break;
            }
        }

        if (idx == -1)
            throw new RuntimeException("Couldn't find the index of the nullable expression's parent");
        return idx;
    }

    public static BlockStmt addCheck(Node exceptionalExpr, IfStmt check) {
        Statement exceptionalExprParentStmt = exceptionalExpr.findAncestor(Statement.class).get();

        BlocksToAddTo blocksToAddTo = getBlockToAddTo(exceptionalExprParentStmt);
        BlockStmt blockToAddTo = blocksToAddTo.blockToAddTo.orElse(null);
        Node nearestAncestor = blocksToAddTo.nearestAncestor;

        int idx = getIndexToInsertAt(blockToAddTo, exceptionalExprParentStmt);

        if (exceptionalExprParentStmt instanceof AssertStmt) {
            blockToAddTo.addStatement(idx, check);
        } else if (exceptionalExprParentStmt instanceof BlockStmt) {
            throw new RuntimeException("BlockStmt shouldn't be the closest of a nullable expression");
        } else if (exceptionalExprParentStmt instanceof BreakStmt) {
            throw new RuntimeException("BreakStmt shouldn't be the closest of a nullable expression");
        } else if (exceptionalExprParentStmt instanceof ContinueStmt) {
            throw new RuntimeException("ContinueStmt shouldn't be the closest of a nullable expression");
        } else if (exceptionalExprParentStmt instanceof DoStmt) {
            throw new RuntimeException("We don't yet hand DoWhile");
        } else if (exceptionalExprParentStmt instanceof EmptyStmt) {
            throw new RuntimeException("EmptyStmt shouldn't be the closest of a nullable expression");
        } else if (exceptionalExprParentStmt instanceof ExplicitConstructorInvocationStmt) {
            throw new RuntimeException(
                    "ExplicitConstructorInvocationStmt shouldn't be the closest of a nullable expression");
        } else if (exceptionalExprParentStmt instanceof ExpressionStmt) {
            // System.out.println("We're in an expression statement");
            if (blockToAddTo == null) {
                // System.out.println("No block to add to");
                // if its in the else, also just add a block stmt
                IfStmt gParent = ((IfStmt) nearestAncestor);
                BlockStmt newBlockStmt = new BlockStmt(new NodeList<Statement>(check));

                Optional<Statement> elseStmt = gParent.getElseStmt();
                if (elseStmt.isPresent() && elseStmt.get().isAncestorOf(exceptionalExpr)) {
                    newBlockStmt.getStatements().add(gParent.getElseStmt().get());
                    gParent.setElseStmt(newBlockStmt);
                } else {
                    newBlockStmt.getStatements().add(gParent.getThenStmt());
                    gParent.setThenStmt(newBlockStmt);
                }

                return newBlockStmt;
            } else {
                // System.out.println("Adding to the block..." + blockToAddTo);
                blockToAddTo.addStatement(idx, check);
                check.setParentNode(blockToAddTo);
                exceptionalExprParentStmt.setParentNode(blockToAddTo);
            }
        } else if (exceptionalExprParentStmt instanceof ForEachStmt) {
            ForEachStmt ogForEachStmt = (ForEachStmt) exceptionalExprParentStmt;
            if (exceptionalExpr.equals(ogForEachStmt.getIterable())
                    || ogForEachStmt.getIterable().isAncestorOf(exceptionalExpr)) {
                blockToAddTo.addStatement(idx, check);
            } else {
                // if the expression is the body, add it there
                BlockStmt body = new BlockStmt(new NodeList<Statement>(ogForEachStmt.getBody()));
                body.addStatement(check);
                ogForEachStmt.setBody(body);
            }
        } else if (exceptionalExprParentStmt instanceof ForStmt) {
            ForStmt ogForStmt = (ForStmt) exceptionalExprParentStmt;
            Expression ogComp = ogForStmt.getCompare().get();
            Expression ogInit = ogForStmt.getInitialization().get(0);
            Expression ogUpdate = ogForStmt.getUpdate().get(0);
            if (exceptionalExpr.equals(ogInit) || ogInit.isAncestorOf(exceptionalExpr)) {
                blockToAddTo.addStatement(idx, check);

            } else if (exceptionalExpr.equals(ogComp) || ogComp.isAncestorOf(exceptionalExpr)) {

                Expression cond = new UnaryExpr(new EnclosedExpr(ogComp), UnaryExpr.Operator.LOGICAL_COMPLEMENT);
                IfStmt newCondition = new IfStmt(cond, new BreakStmt(), null);

                NodeList<Statement> newStmts = new NodeList<Statement>();
                newStmts.add(check);
                newStmts.add(newCondition);
                // newStmts.add(newHoistedDecl);
                if (ogForStmt.getBody() instanceof BlockStmt) {
                    newStmts.addAll(((BlockStmt) ogForStmt.getBody()).getStatements());
                } else {
                    newStmts.add(ogForStmt.getBody());
                }

                BlockStmt newBody = new BlockStmt(newStmts);

                ForStmt newFor = new ForStmt(ogForStmt.getInitialization(), new BooleanLiteralExpr(true),
                        ogForStmt.getUpdate(), newBody);
                blockToAddTo.replace(ogForStmt, newFor);

            } else if (exceptionalExpr.equals(ogUpdate) || ogUpdate.isAncestorOf(exceptionalExpr)) {

                String varName = String.valueOf("_var_is_first_itr");
                VariableDeclarator vd_update = new VariableDeclarator(PrimitiveType.booleanType(), varName,
                        new BooleanLiteralExpr(false));
                ExpressionStmt vdeUpdate = new ExpressionStmt(new VariableDeclarationExpr(vd_update));

                BlockStmt thenStmt = new BlockStmt(new NodeList<Statement>(new ExpressionStmt(
                        new AssignExpr(new NameExpr(varName), new BooleanLiteralExpr(true), Operator.ASSIGN))));
                BlockStmt elseStmt = new BlockStmt(new NodeList<Statement>(check, new ExpressionStmt(ogUpdate)));

                IfStmt updateIfStmt = new IfStmt(
                        new UnaryExpr(new NameExpr(varName), UnaryExpr.Operator.LOGICAL_COMPLEMENT), thenStmt,
                        elseStmt);

                Expression cond = new UnaryExpr(new EnclosedExpr(ogComp), UnaryExpr.Operator.LOGICAL_COMPLEMENT);
                IfStmt newCondition = new IfStmt(cond, new BreakStmt(), null);

                // int lastStmt = blockToAddTo.getStatements().size() - 1;

                blockToAddTo.addStatement(idx, vdeUpdate);
                NodeList<Statement> newStmts = new NodeList<Statement>(updateIfStmt, newCondition);

                if (ogForStmt.getBody() instanceof BlockStmt) {
                    newStmts.addAll(((BlockStmt) ogForStmt.getBody()).getStatements());
                } else {
                    newStmts.add(ogForStmt.getBody());
                }

                BlockStmt newBody = new BlockStmt(newStmts);
                ForStmt newFor = new ForStmt(ogForStmt.getInitialization(), ogForStmt.getCompare().get(),
                        new NodeList<Expression>(), newBody);

                blockToAddTo.replace(ogForStmt, newFor);
                // hoistedExprParentStmt.asForStmt().getBody().addStatement(lastStmt,
                // newHoistedDecl);

            } else {

                blockToAddTo.addStatement(idx, check);
            }
        } else if (exceptionalExprParentStmt instanceof IfStmt) {
            IfStmt ogIfStmt = (IfStmt) exceptionalExprParentStmt;

            // first just add blocks
            if (!(ogIfStmt.getThenStmt() instanceof BlockStmt)) {
                Statement thenStmt = ogIfStmt.getThenStmt();
                BlockStmt thenBlock = new BlockStmt(new NodeList<Statement>(thenStmt));

                // ogIfStmt.getThenStmt().setParentNode(thenBlock);
                ogIfStmt.setThenStmt(thenBlock);
                thenStmt.setParentNode(thenBlock);
            }

            if (ogIfStmt.getElseStmt().isPresent() && !(ogIfStmt.getElseStmt().get() instanceof BlockStmt)) {
                Statement elseStmt = ogIfStmt.getElseStmt().get();
                NodeList<Statement> stmts = new NodeList<Statement>(elseStmt);
                BlockStmt elseBlock = new BlockStmt(stmts);
                // stmts.setParentNode(elseBlock);
                ogIfStmt.setElseStmt(elseBlock);
                elseStmt.setParentNode(elseBlock);

            }

            // If the expression is in an else if condition, we need to turn it into a
            // nested if..
            if (Utils.isElseIf(ogIfStmt, exceptionalExpr)) {
                // add the check as the last stmt of the blockToAddTo body
                // body stays the same
                // assert (false);
                assert (blockToAddTo == null);

                IfStmt elseStmt = ogIfStmt;

                Statement ogElse = elseStmt.getElseStmt().isPresent() ? elseStmt.getElseStmt().get() : null;
                // Statement ogElse = elseStmt.getElseStmt().get();

                IfStmt newIfStmt = new IfStmt(elseStmt.getCondition(), elseStmt.getThenStmt(), ogElse);
                // elseStmt.setParentNode(newIfStmt);

                NodeList<Statement> newStmts = new NodeList<Statement>();
                newStmts.add(check);
                newStmts.add(newIfStmt);

                // newStmts.addAll(((BlockStmt) elseStmt.getThenStmt()).getStatements());
                IfStmt parent = (IfStmt) ogIfStmt.getParentNode().get();
                BlockStmt newElseStmt = new BlockStmt(newStmts);
                newIfStmt.setParentNode(newElseStmt);

                parent.setElseStmt(newElseStmt);

                return new BlockStmt(new NodeList<Statement>(newIfStmt));

            } else if (exceptionalExpr.equals(ogIfStmt.getCondition())
                    || ogIfStmt.getCondition().isAncestorOf(exceptionalExpr)) {
                // If the expression is in the condition, hoist it above
                // if (Utils.DEBUG)
                // System.out.println("Hoisted expr is in the condition");
                blockToAddTo.addStatement(idx, check);

            } else {
                // if the expression is in the body, add it there
                NodeList<Statement> newStmts = new NodeList<Statement>();
                newStmts.add(check);
                // if (ogIfStmt.getThenStmt() instanceof BlockStmt) {
                newStmts.addAll(((BlockStmt) ogIfStmt.getThenStmt()).getStatements());
                // } else {
                // newStmts.add(ogIfStmt.getThenStmt());
                // }

                ogIfStmt.setThenStmt(new BlockStmt(newStmts));
            }
        } else if (exceptionalExprParentStmt instanceof LabeledStmt) {
            throw new RuntimeException("Don't yet handle labeled stmts");
        } else if (exceptionalExprParentStmt instanceof LocalClassDeclarationStmt) {
            throw new RuntimeException("Don't yet handle local class declaration stmts");
        } else if (exceptionalExprParentStmt instanceof LocalRecordDeclarationStmt) {
            throw new RuntimeException("Don't yet handle local record declaration stmts");
        } else if (exceptionalExprParentStmt instanceof ReturnStmt) {
            throw new RuntimeException("There should never be a field access in a return");
        } else if (exceptionalExprParentStmt instanceof SwitchStmt) {
            throw new RuntimeException("Don't yet handle switch stmts");
        } else if (exceptionalExprParentStmt instanceof SynchronizedStmt) {
            throw new RuntimeException("Don't yet handle synchronized stmts");
        } else if (exceptionalExprParentStmt instanceof ThrowStmt) {
            throw new RuntimeException("Don't yet handle throw stmts");
        } else if (exceptionalExprParentStmt instanceof TryStmt) {
            throw new RuntimeException("TryStmt shouldn't be the closest parent to a check.. It should be a block");
        } else if (exceptionalExprParentStmt instanceof UnparsableStmt) {
            throw new RuntimeException("UnparsableStatements shouldn't be the closest parent to a check");
        } else if (exceptionalExprParentStmt instanceof WhileStmt) {
            if (exceptionalExpr.equals(((WhileStmt) exceptionalExprParentStmt).getCondition()) ||
                    ((WhileStmt) exceptionalExprParentStmt).getCondition().isAncestorOf(exceptionalExpr)) {

                Expression oldCondition = ((WhileStmt) exceptionalExprParentStmt).getCondition();
                // wrap oldCondition in parens

                Expression newCondition = new UnaryExpr(new EnclosedExpr(oldCondition),
                        UnaryExpr.Operator.LOGICAL_COMPLEMENT);

                IfStmt newIfStmt = new IfStmt(newCondition, new BlockStmt(new NodeList<Statement>(new BreakStmt())),
                        null);

                Statement newBody = ((WhileStmt) exceptionalExprParentStmt).getBody();

                NodeList<Statement> newBodyStmts = new NodeList<Statement>(check, newIfStmt);
                if (newBody instanceof BlockStmt) {
                    newBodyStmts.addAll(((BlockStmt) newBody).getStatements());
                } else {
                    newBodyStmts.add(newBody);
                }

                WhileStmt newLoop = new WhileStmt(new BooleanLiteralExpr(true),
                        new BlockStmt(newBodyStmts));

                // blockToAddTo.addStatement(idx, vdeNoInit);
                blockToAddTo.replace(exceptionalExprParentStmt, newLoop);

            } else {
                // if the hoisted expression is in the body, then just add it to the body
                blockToAddTo.addStatement(idx, check);
            }
        } else if (exceptionalExprParentStmt instanceof YieldStmt) {

        } else {
            throw new RuntimeException(
                    "ERROR: Unhandled Statement type " + exceptionalExprParentStmt.getClass().getName());

        }
        // block.addStatement(idx, nullCheck);
        // this.nullChecks.add(nullableExpr.toString());
        return blockToAddTo;

    }
}
