public class SymbolTableVisitor extends MicroBaseVisitor<Void>{

    private SymbolTable symbolTable = new SymbolTable();

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    @Override public Void visitStringDeclRule(MicroParser.StringDeclRuleContext ctx) {
        String name = ctx.name.getText();
        String value = ctx.value.getText();
        symbolTable.addSymbol("STRING", name, value);
        return visitChildren(ctx);
    }

    @Override public Void visitVariableDeclRule(MicroParser.VariableDeclRuleContext ctx) {
        String[] names = ctx.names.getText().split(",");
        String type = ctx.type.getText();
        for (String name : names) {
            symbolTable.addSymbol(type, name);
        }
        return visitChildren(ctx);
    }

    @Override public Void visitParameterDeclRule(MicroParser.ParameterDeclRuleContext ctx) {
        String name = ctx.name.getText();
        String type = ctx.type.getText();
        symbolTable.addSymbol(type, name);
        return visitChildren(ctx);
    }

    @Override public Void visitFuncDeclRule(MicroParser.FuncDeclRuleContext ctx) {
        symbolTable.openScope(ctx.name.getText());
        visitChildren(ctx);
        symbolTable.closeScope();
        return null;
    }

    @Override public Void visitIfStmtRule(MicroParser.IfStmtRuleContext ctx) {
        symbolTable.openScope();
        visitChildren(ctx);
        symbolTable.closeScope();
        return null;
    }

    @Override public Void visitElseStmtRule(MicroParser.ElseStmtRuleContext ctx) {
        symbolTable.openScope();
        visitChildren(ctx);
        symbolTable.closeScope();
        return null;
    }

    @Override public Void visitForStmtRule(MicroParser.ForStmtRuleContext ctx) {
        symbolTable.openScope();
        visitChildren(ctx);
        symbolTable.closeScope();
        return null;
    }
}
