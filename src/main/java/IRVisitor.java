import javax.sql.rowset.spi.SyncProvider;

public class IRVisitor extends MicroBaseVisitor<Symbol> {

    private SymbolTable symbolTable;
    private int symbolCounter = 0;
    private int labelCounter = 0;
    private IntermediateRepresentation intermediateRepresentation;

    private Symbol newSymbol(String type) {
        String name = "$T" + (++symbolCounter);
        return new Symbol(type, name);
    }

    private Symbol newLabel() {
        String name = "L" + (++labelCounter);
        return new Symbol("LABEL", name);
    }

    public IRVisitor(SymbolTable symbolTable) {
        super();
        this.symbolTable = symbolTable;
        this.intermediateRepresentation = new IntermediateRepresentation();
    }

    public IntermediateRepresentation getIntermediateRepresentation() {
        return intermediateRepresentation;
    }

    @Override
    public Symbol visitReadStmtRule(MicroParser.ReadStmtRuleContext ctx) {
        String[] names = ctx.names.getText().split(",");
        for (String name: names) {
            String type = symbolTable.getType(name);
            if (type.equals("INT")) {
                intermediateRepresentation.addStatement("READI  " + name);
            } else if (type.equals("FLOAT")) {
                intermediateRepresentation.addStatement("READF  " + name);
            }
        }
        visitChildren(ctx);
        return null;
    }

    @Override
    public Symbol visitWriteStmtRule(MicroParser.WriteStmtRuleContext ctx) {
        String[] names = ctx.names.getText().split(",");
        for (String name: names) {
            String type = symbolTable.getType(name);
            if (type.equals("INT")) {
                intermediateRepresentation.addStatement("WRITEI  " + name);
            } else if (type.equals("FLOAT")) {
                intermediateRepresentation.addStatement("WRITEF  " + name);
            } else if (type.equals("STRING")) {
                intermediateRepresentation.addStatement("WRITES  " + name);
            }
        }
        visitChildren(ctx);
        return null;
    }

    @Override
    public Symbol visitExprRule(MicroParser.ExprRuleContext ctx) {
        Symbol pre = visit(ctx.pre);
        Symbol ter = visit(ctx.ter);
        return visitMathTerm(pre, ter, ctx.pre.getText());
    }

    @Override
    public Symbol visitExprPrefixRule(MicroParser.ExprPrefixRuleContext ctx) {
        Symbol pre = visit(ctx.pre);
        Symbol ter = visit(ctx.ter);
        return visitMathTerm(pre, ter, ctx.pre.getText());
    }

    @Override
    public Symbol visitTermRule(MicroParser.TermRuleContext ctx) {
        Symbol pre = visit(ctx.pre);
        Symbol fac = visit(ctx.fac);
        return visitMathTerm(pre, fac, ctx.pre.getText());
    }

    @Override
    public Symbol visitFactorPrimaryRule(MicroParser.FactorPrimaryRuleContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Symbol visitFactorPrefixRule(MicroParser.FactorPrefixRuleContext ctx) {
        Symbol pre = visit(ctx.pre);
        Symbol fac = visit(ctx.fac);
        return visitMathTerm(pre, fac, ctx.pre.getText());
    }

    @Override
    public Symbol visitPrimaryExprRule(MicroParser.PrimaryExprRuleContext ctx) {
        return visit(ctx.exp);
    }

    @Override
    public Symbol visitPrimaryFLOATRule(MicroParser.PrimaryFLOATRuleContext ctx) {
        return new Symbol("FLOAT", ctx.getText());
    }

    @Override
    public Symbol visitPrimaryIDRule(MicroParser.PrimaryIDRuleContext ctx) {
        return symbolTable.getSymbol(ctx.name.getText());
    }

    @Override
    public Symbol visitPrimaryINTRule(MicroParser.PrimaryINTRuleContext ctx) {
        return new Symbol("INT", ctx.getText());
    }

    private Symbol visitMathTerm(Symbol pre, Symbol arg, String preText) {
        if (pre == null) {
            return arg;
        } else {
            String type1 = pre.getType();
            String type2 = arg.getType();
            String operation;
            Symbol symbol;
            if (preText.endsWith("+")) {
                operation = "ADD";
            } else if (preText.endsWith("-")) {
                operation = "SUB";
            } else if (preText.endsWith("*")) {
                operation = "MULT";
            } else {
                operation = "DIV";
            }

            if (type1.equals("FLOAT") || type2.equals("FLOAT")) {
                operation = operation + "F";
                symbol = newSymbol("FLOAT");
            } else {
                operation = operation + "I";
                symbol = newSymbol("INT");
            }

            intermediateRepresentation.addStatement(operation + "  " + pre.getName() +
                    "  " + arg.getName() + "  " + symbol.getName());
            return symbol;
        }
    }

    @Override
    public Symbol visitAssignExprRule(MicroParser.AssignExprRuleContext ctx) {
        String name = ctx.name.getText();
        Symbol symbol = visit(ctx.exp);
        if (symbol.getType().equals("INT")) {
            intermediateRepresentation.addStatement("STOREI  " + symbol.getName() + "  " + name);
        } else if (symbol.getType().equals("FLOAT")) {
            intermediateRepresentation.addStatement("STOREF  " + symbol.getName() + "  " + name);
        }
        return null;
    }

    @Override
    public Symbol visitForStmtRule(MicroParser.ForStmtRuleContext ctx) {
        visit(ctx.ini);
        Symbol label1 = newLabel();
        intermediateRepresentation.addStatement("LABEL  " + label1.getName());
        Symbol label2 = visit(ctx.con);
        visit(ctx.dec);
        visit(ctx.stm);
        visit(ctx.exp);
        intermediateRepresentation.addStatement("JUMP  " + label1.getName());
        intermediateRepresentation.addStatement("LABEL  " + label2.getName());
        return null;
    }

    @Override
    public Symbol visitCondRule(MicroParser.CondRuleContext ctx) {
        Symbol symbol1 = visit(ctx.exp1);
        Symbol symbol2 = visit(ctx.exp2);
        String op = "";
        if (ctx.com.getText().equals("=")) {
            op = "NE";
        } else if (ctx.com.getText().equals("!=")) {
            op = "EQ";
        } else if (ctx.com.getText().equals("<=")) {
            op = "GT";
        } else if (ctx.com.getText().equals(">=")) {
            op = "LT";
        } else if (ctx.com.getText().equals("<")) {
            op = "GE";
        } else if (ctx.com.getText().equals(">")) {
            op = "LE";
        }
        Symbol label = newLabel();
        intermediateRepresentation.addStatement(op + "  " + symbol1.getName() + "  " +
                symbol2.getName() + "  " + label.getName());
        return label;
    }

    @Override
    public Symbol visitIfStmtRule(MicroParser.IfStmtRuleContext ctx) {
        Symbol label1 = visit(ctx.con);
        visit(ctx.dec);
        visit(ctx.stm);
        if (ctx.els.getText().equals("")) {
            intermediateRepresentation.addStatement("LABEL  " + label1.getName());
        } else {
            Symbol label2 = newLabel();
            intermediateRepresentation.addStatement("JUMP  " + label2.getName());
            intermediateRepresentation.addStatement("LABEL  " + label1.getName());
            visit(ctx.els);
            intermediateRepresentation.addStatement("LABEL  " + label2.getName());
        }
        return null;
    }
}
