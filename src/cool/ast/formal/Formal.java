package cool.ast.formal;

import cool.ast.ASTNode;
import cool.ast.ASTVisitor;
import cool.ast.expression.ObjectId;
import cool.ast.type.TypeId;
import cool.structures.FormalSymbol;
import cool.structures.Scope;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class Formal extends ASTNode {
    private final ObjectId objectId;
    private final TypeId typeId;
    private FormalSymbol formalSymbol;
    private Scope resolutionScope;

    public Formal(Token token, ParserRuleContext parserRuleContext,
                  ObjectId objectId, TypeId typeId) {
        super(token, parserRuleContext);
        this.objectId = objectId;
        this.typeId = typeId;
    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public TypeId getTypeId() {
        return typeId;
    }

    public Scope getResolutionScope() {
        return resolutionScope;
    }

    public void setResolutionScope(Scope resolutionScope) {
        this.resolutionScope = resolutionScope;
    }

    public FormalSymbol getFormalSymbol() {
        return formalSymbol;
    }

    public void setFormalSymbol(FormalSymbol formalSymbol) {
        this.formalSymbol = formalSymbol;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
