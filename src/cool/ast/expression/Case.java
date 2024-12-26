package cool.ast.expression;

import cool.ast.ASTVisitor;
import cool.ast.branch.Branch;
import cool.structures.CaseSymbol;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;

public class Case extends Expression {
    private final Expression caseExpr;
    private final ArrayList<Branch> branches;
    private CaseSymbol caseSymbol;

    public Case(Token token, ParserRuleContext parserRuleContext,
                Expression caseExpr, ArrayList<Branch> branches) {
        super(token, parserRuleContext);
        this.caseExpr = caseExpr;
        this.branches = branches;
    }

    public Expression getCaseExpr() {
        return caseExpr;
    }

    public ArrayList<Branch> getBranches() {
        return branches;
    }

    public CaseSymbol getCaseSymbol() {
        return caseSymbol;
    }

    public void setCaseSymbol(CaseSymbol caseSymbol) {
        this.caseSymbol = caseSymbol;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
