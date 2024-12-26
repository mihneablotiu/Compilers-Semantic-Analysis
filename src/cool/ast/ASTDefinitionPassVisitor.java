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
import java.util.stream.Stream;

public class ASTDefinitionPassVisitor implements ASTVisitor<TypeSymbol> {
    Scope currentScope = SymbolTable.globals;

    @Override
    public TypeSymbol visit(Branch branch) {
        ObjectId branchObjectId = branch.getObjectId();
        TypeId branchType = branch.getTypeId();

        BranchSymbol branchSymbol = new BranchSymbol(branchObjectId.getToken().getText(), currentScope);

        if (branchObjectId.getToken().getText().equals("self")) {
            SymbolTable.error(branch.getParserRuleContext(), branchObjectId.getToken(),
                    "Case variable has illegal name self");
            return null;
        }

        if (branchType.getToken().getText().equals("SELF_TYPE")) {
            SymbolTable.error(branch.getParserRuleContext(), branchType.getToken(),
                    "Case variable " + branchObjectId.getToken().getText() + " has illegal type SELF_TYPE");
            return null;
        }

        branch.setBranchSymbol(branchSymbol);
        branch.setResolutionScope(currentScope);

        currentScope = branchSymbol;

        branch.getBranch().accept(this);

        currentScope = currentScope.getParent();

        return null;
    }

    @Override
    public TypeSymbol visit(ClassNode classNode) {
        TypeId className = classNode.getClassName();
        TypeId parentName = classNode.getParentName();

        ClassSymbol classSymbol = new ClassSymbol(className.getToken().getText(), currentScope);

        if (!currentScope.add(classSymbol)) {
            SymbolTable.error(classNode.getParserRuleContext(), className.getToken(),
                    "Class " + className.getToken().getText() + " is redefined");

            return null;
        }

        if (className.getToken().getText().equals("SELF_TYPE")) {
            SymbolTable.error(classNode.getParserRuleContext(), className.getToken(),
                    "Class has illegal name SELF_TYPE");
            return null;
        }

        if (parentName != null) {
            if (!Stream.of("Int", "String", "Bool", "SELF_TYPE")
                    .filter((elem) -> elem.equals(parentName.getToken().getText()))
                    .toList().isEmpty()) {
                SymbolTable.error(classNode.getParserRuleContext(), parentName.getToken(),
                        "Class " + className.getToken().getText() + " has illegal parent " +
                                parentName.getToken().getText());

                return null;
            }
        }

        classNode.setClassSymbol(classSymbol);

        currentScope = classSymbol;

        classNode.getFeatures().forEach((feature) -> feature.accept(this));

        currentScope = currentScope.getParent();

        return null;
    }

    @Override
    public TypeSymbol visit(Arithmetic arithmetic) {
        return null;
    }

    @Override
    public TypeSymbol visit(Assign assign) {
        assign.getExpr().accept(this);
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
        CaseSymbol caseSymbol = new CaseSymbol(String.valueOf(Math.random()));
        caseExpr.setCaseSymbol(caseSymbol);

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
        LetSymbol newLetSymbol = new LetSymbol(String.valueOf(Math.random()), currentScope);
        let.setLetSymbol(newLetSymbol);

        currentScope.add(newLetSymbol);

        for (int i = 0; i < let.getLocals().size(); i++) {
            let.getLocals().get(i).accept(this);

            currentScope = newLetSymbol;

            newLetSymbol = new LetSymbol(String.valueOf(Math.random()), currentScope);
        }

        let.getLetExpr().accept(this);

        for (int i = 0; i < let.getLocals().size(); i++) {
            currentScope = currentScope.getParent();
        }

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
        objectId.setResolutionScope(currentScope);
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
        String className = ((ClassSymbol) currentScope).getName();

        AttributeSymbol attributeSymbol = new AttributeSymbol(fieldId.getToken().getText());

        if (fieldId.getToken().getText().equals("self")) {
            SymbolTable.error(field.getParserRuleContext(), fieldId.getToken(),
                    "Class " +  className + " has attribute with illegal name self");

            return null;
        }

        if (!currentScope.add(attributeSymbol)) {
            SymbolTable.error(field.getParserRuleContext(), field.getToken(),
                    "Class " + className + " redefines attribute " + fieldId.getToken().getText());
            return null;
        }

        field.setAttributeSymbol(attributeSymbol);
        field.setResolutionScope(currentScope);

        return null;
    }

    @Override
    public TypeSymbol visit(Method method) {
        ObjectId methodId = method.getMethodId();
        String className = ((ClassSymbol) currentScope).getName();

        MethodSymbol methodSymbol = new MethodSymbol(methodId.getToken().getText(), currentScope);

        if (!currentScope.add(methodSymbol)) {
            SymbolTable.error(method.getParserRuleContext(), method.getToken(),
                    "Class " + className + " redefines method " + methodId.getToken().getText());

            return null;
        }

        method.setMethodSymbol(methodSymbol);
        method.setResolutionScope(currentScope);

        currentScope = methodSymbol;

        method.getFormals().forEach((formal) -> formal.accept(this));
        method.getInsideExpr().accept(this);

        currentScope = methodSymbol.getParent();

        return null;
    }

    @Override
    public TypeSymbol visit(Local local) {
        ObjectId localName = local.getObjectId();

        LocalSymbol localSymbol = new LocalSymbol(local.getObjectId().getToken().getText());
;
        if (localName.getToken().getText().equals("self")) {
            SymbolTable.error(localName.getParserRuleContext(), localName.getToken(),
                    "Let variable has illegal name self");
            return null;
        }

        currentScope.add(localSymbol);

        local.setLocalSymbol(localSymbol);
        local.setResolutionScope(currentScope);

        if (local.getAssignExpr() != null)
            local.getAssignExpr().accept(this);

        return null;
    }

    @Override
    public TypeSymbol visit(Formal formal) {
        ObjectId formalId = formal.getObjectId();
        TypeId formalType = formal.getTypeId();
        String methodName = ((MethodSymbol) currentScope).getName();
        String className =  ((ClassSymbol) currentScope.getParent()).getName();

        FormalSymbol formalSymbol = new FormalSymbol(formalId.getToken().getText());

        if (!currentScope.add(formalSymbol)) {
            SymbolTable.error(formal.getParserRuleContext(), formal.getToken(),
                    "Method " + methodName + " of class " + className + " redefines formal parameter " + formalId.getToken().getText());

            return null;
        }

        if (formalId.getToken().getText().equals("self")) {
            SymbolTable.error(formal.getParserRuleContext(), formal.getToken(),
                    "Method " + methodName + " of class " + className + " has formal parameter " +
                            "with illegal name self");
            return null;
        }

        if (formalType.getToken().getText().equals("SELF_TYPE")) {
            SymbolTable.error(formal.getParserRuleContext(), formalType.getToken(),
                    "Method " + methodName + " of class " + className + " has formal parameter " +
                            formalId.getToken().getText() + " with illegal type SELF_TYPE");
            return null;
        }

        formal.setFormalSymbol(formalSymbol);
        formal.setResolutionScope(currentScope);

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
