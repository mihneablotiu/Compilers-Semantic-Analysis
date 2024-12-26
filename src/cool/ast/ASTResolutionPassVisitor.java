package cool.ast;

import cool.ast.branch.Branch;
import cool.ast.classNode.ClassNode;
import cool.ast.expression.*;
import cool.ast.feature.Field;
import cool.ast.feature.Method;
import cool.ast.formal.Formal;
import cool.ast.local.Local;
import cool.ast.program.Program;
import cool.ast.type.TypeId;
import cool.structures.*;

import java.util.Iterator;
import java.util.Map;

public class ASTResolutionPassVisitor implements ASTVisitor<TypeSymbol> {
    Scope globalScope = SymbolTable.globals;

    @Override
    public TypeSymbol visit(Branch branch) {
        ObjectId branchObjectId = branch.getObjectId();
        TypeId branchType = branch.getTypeId();

        if (branch.getResolutionScope() != null) {
            Symbol classSymbol = globalScope.lookup(branchType.getToken().getText());
            if (!(classSymbol instanceof ClassSymbol)) {
                SymbolTable.error(branch.getParserRuleContext(), branchType.getToken(),
                        "Case variable " + branchObjectId.getToken().getText() + " has undefined type " +
                        branchType.getToken().getText());

                return null;
            }
        }

        branch.getBranch().accept(this);
        return null;
    }

    @Override
    public TypeSymbol visit(ClassNode classNode) {
        TypeId className = classNode.getClassName();
        TypeId parentName = classNode.getParentName();

        if (classNode.getClassSymbol() != null) {
            if (parentName != null) {
                ClassSymbol parentSymbol = (ClassSymbol) globalScope.lookup(parentName.getToken().getText());

                if (parentSymbol == null) {
                    SymbolTable.error(classNode.getParserRuleContext(), parentName.getToken(),
                            "Class " + className.getToken().getText() + " has undefined parent " +
                                    parentName.getToken().getText());

                    return null;
                }

                classNode.getClassSymbol().setDirectParent(parentSymbol);
            } else {
                classNode.getClassSymbol().setDirectParent((ClassSymbol) SymbolTable.globals.lookup("Object"));
            }

            classNode.getFeatures().forEach((feature) -> feature.accept(this));

            return null;
        }

        return null;
    }

    @Override
    public TypeSymbol visit(Arithmetic arithmetic) {
        return null;
    }

    @Override
    public TypeSymbol visit(Assign assign) {
        return null;
    }

    @Override
    public TypeSymbol visit(Block block) {
        return null;
    }

    @Override
    public TypeSymbol visit(Bool bool) {
        return null;
    }

    @Override
    public TypeSymbol visit(Case caseExpr) {
        caseExpr.getCaseExpr().accept(this);
        caseExpr.getBranches().forEach((branch) -> branch.accept(this));

        return null;
    }

    @Override
    public TypeSymbol visit(ExplicitCall explicitCall) {
        return null;
    }

    @Override
    public TypeSymbol visit(If ifExpr) {
        return null;
    }

    @Override
    public TypeSymbol visit(ImplicitCall implicitCall) {
        return null;
    }

    @Override
    public TypeSymbol visit(Int intExpr) {
        return null;
    }

    @Override
    public TypeSymbol visit(Let let) {
        let.getLocals().forEach((local) -> local.accept(this));
        let.getLetExpr().accept(this);
        return null;
    }

    @Override
    public TypeSymbol visit(New newExpr) {
        return null;
    }

    @Override
    public TypeSymbol visit(Logical logical) {
        return null;
    }

    @Override
    public TypeSymbol visit(ObjectId objectId) {
        String objectName = objectId.getToken().getText();
        ObjectIdSymbol objectIdSymbol = (ObjectIdSymbol) objectId.getResolutionScope().lookup(objectName);

        if (objectIdSymbol == null) {
            SymbolTable.error(objectId.getParserRuleContext(), objectId.getToken(), "Undefined identifier " + objectName);
            return null;
        }

        return null;
    }

    @Override
    public TypeSymbol visit(Paren paren) {
        return null;
    }

    @Override
    public TypeSymbol visit(Str str) {
        return null;
    }

    @Override
    public TypeSymbol visit(Unary unary) {
        return null;
    }

    @Override
    public TypeSymbol visit(While whileExpr) {
        return null;
    }

    @Override
    public TypeSymbol visit(Field field) {
        ObjectId fieldId = field.getFieldId();

        if (field.getResolutionScope() != null) {
            String className = ((ClassSymbol) field.getResolutionScope()).getName();

            Symbol symbolInParents = ((ClassSymbol) field.getResolutionScope()).getDirectParent().lookup(fieldId.getToken().getText());
            if (symbolInParents instanceof AttributeSymbol) {
                SymbolTable.error(field.getParserRuleContext(), field.getToken(),
                        "Class " + className + " redefines inherited attribute " + fieldId.getToken().getText());

                return null;
            }


            Symbol classSymbol = globalScope.lookup(field.getTypeId().getToken().getText());

            if (!(classSymbol instanceof ClassSymbol)) {
                SymbolTable.error(field.getParserRuleContext(), field.getTypeId().getToken(),
                        "Class " + className + " has attribute " + fieldId.getToken().getText() + " with undefined type " + field.getTypeId().getToken().getText());

                return null;
            }

            field.getAttributeSymbol().setType((ClassSymbol) classSymbol);
        }

        return null;
    }

    @Override
    public TypeSymbol visit(Method method) {
        ObjectId methodId = method.getMethodId();
        TypeId returnType = method.getReturnType();

        if (method.getResolutionScope() != null) {
            String className = ((ClassSymbol) method.getResolutionScope()).getName();;

            Symbol classSymbol = globalScope.lookup(returnType.getToken().getText());
            if (!(classSymbol instanceof ClassSymbol)) {
                SymbolTable.error(method.getParserRuleContext(), returnType.getToken(),
                        "Class " + className + " has method " + methodId.getToken().getText() +
                        " with undefined return type " + returnType.getToken().getText());

                return null;
            }

            method.getMethodSymbol().setType((ClassSymbol) classSymbol);

            MethodSymbol similarMethod = (MethodSymbol) ((ClassSymbol) method.getResolutionScope()).getDirectParent().lookup(methodId.getToken().getText());

            if (similarMethod != null && similarMethod.getSymbols().size() != method.getFormals().size()) {
                SymbolTable.error(method.getParserRuleContext(), method.getToken(),
                        "Class " + className + " overrides method " + methodId.getToken().getText() + " with" +
                                " different number of formal parameters");

                return null;
            }
        }

        method.getFormals().forEach((formal) -> formal.accept(this));
        method.getInsideExpr().accept(this);
        return null;
    }

    @Override
    public TypeSymbol visit(Local local) {
        if (local.getResolutionScope() != null) {
            Symbol classSymbol = globalScope.lookup(local.getTypeId().getToken().getText());
            if (!(classSymbol instanceof ClassSymbol)) {
                SymbolTable.error(local.getParserRuleContext(), local.getTypeId().getToken(),
                        "Let variable " + local.getObjectId().getToken().getText() + " has undefined type "
                                + local.getTypeId().getToken().getText());

                return null;
            }
        }
        return null;
    }

    @Override
    public TypeSymbol visit(Formal formal) {
        ObjectId formalId = formal.getObjectId();
        TypeId formalType = formal.getTypeId();

        if (formal.getResolutionScope() != null) {
            String methodName = ((MethodSymbol) formal.getResolutionScope()).getName();
            String className = ((ClassSymbol) formal.getResolutionScope().getParent()).getName();

            Symbol classSymbol = globalScope.lookup(formalType.getToken().getText());
            if (!(classSymbol instanceof ClassSymbol)) {
                SymbolTable.error(formal.getParserRuleContext(), formal.getTypeId().getToken(),
                        "Method " + methodName + " of class " + className + " has formal parameter " +
                                formalId.getToken().getText() + " with undefined type " + formalType.getToken().getText());

                return null;
            }

            formal.getFormalSymbol().setType((ClassSymbol) classSymbol);
        }

        return null;
    }

    @Override
    public TypeSymbol visit(Program program) {
        program.getClasses().forEach((classNode) -> classNode.accept(this));

        return null;
    }

    @Override
    public TypeSymbol visit(TypeId typeId) {
        return null;
    }
}
