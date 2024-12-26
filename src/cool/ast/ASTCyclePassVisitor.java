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

public class ASTCyclePassVisitor implements ASTVisitor<TypeSymbol> {
    Scope globalScope = SymbolTable.globals;

    @Override
    public TypeSymbol visit(Branch branch) {
        return null;
    }

    @Override
    public TypeSymbol visit(ClassNode classNode) {
        TypeId className = classNode.getClassName();
        TypeId parentName = classNode.getParentName();

        if (classNode.getClassSymbol() != null) {
            if (parentName != null && globalScope.lookup(parentName.getToken().getText()) != null) {
                if (((ClassSymbol) classNode.getClassSymbol().getDirectParent()).isInCycle(className.getToken().getText())) {
                    SymbolTable.error(classNode.getParserRuleContext(), className.getToken(),
                            "Inheritance cycle for class " + className.getToken().getText());

                    return null;
                }
            }
        }

        classNode.getFeatures().forEach((feature) -> feature.accept(this));
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
        return null;
    }

    @Override
    public TypeSymbol visit(Method method) {
        ObjectId methodId = method.getMethodId();

        if (method.getResolutionScope() != null) {
            String className = ((ClassSymbol) method.getResolutionScope()).getName();;

            MethodSymbol similarMethod = (MethodSymbol) ((ClassSymbol) method.getResolutionScope()).getDirectParent().lookup(methodId.getToken().getText());

            if (similarMethod != null && similarMethod.getSymbols().size() == method.getFormals().size()) {
                Iterator<Map.Entry<String, Symbol>> iter1 = similarMethod.getSymbols().entrySet().iterator();
                Iterator<Map.Entry<String, Symbol>> iter2 = method.getMethodSymbol().getSymbols().entrySet().iterator();

                int i = 0;
                while (iter1.hasNext() || iter2.hasNext()) {
                    Map.Entry<String, Symbol> e1 = iter1.next();
                    Map.Entry<String, Symbol> e2 = iter2.next();

                    FormalSymbol oldSymbol = (FormalSymbol) e1.getValue();
                    FormalSymbol newSymbol = (FormalSymbol) e2.getValue();

                    if (!oldSymbol.getType().getName().equals(newSymbol.getType().getName())) {
                        SymbolTable.error(method.getParserRuleContext(), method.getFormals().get(i).getTypeId().getToken(),
                                "Class " + className + " overrides method " + methodId.getToken().getText() + " but" +
                                        " changes type of formal parameter " + newSymbol.getName() + " from " + oldSymbol.getType().getName()
                                        + " to " + newSymbol.getType().getName());

                        return null;
                    }

                    i++;
                }
            }

            if (similarMethod != null && method.getMethodSymbol().getType() != null
                    && !similarMethod.getType().getName().equals(method.getMethodSymbol().getType().getName())) {
                SymbolTable.error(method.getParserRuleContext(), method.getReturnType().getToken(),
                        "Class " + className + " overrides method " + methodId.getToken().getText() + " but " +
                                "changes return type from " + similarMethod.getType().getName() + " to " + method.getMethodSymbol().getType().getName());

                return null;
            }
        }

        method.getFormals().forEach((formal) -> formal.accept(this));
        return null;
    }

    @Override
    public TypeSymbol visit(Local local) {
        return null;
    }

    @Override
    public TypeSymbol visit(Formal formal) {
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
