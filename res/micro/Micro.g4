grammar Micro;

COMMENT : [\-][\-].*?('\r'|'\n') -> skip;

KEYWORD: (
    'PROGRAM' |
    'BEGIN' |
    'END' |
    'FUNCTION' |
    'RETURN' |
    'READ' |
    'WRITE' |
    'IF' |
    'ELSE' |
    'ENDIF' |
    'FOR' |
    'ENDFOR' |
    'INT' |
    'VOID' |
    'STRING' |
    'FLOAT'
);

OPERATOR: (
    ':=' |
    '+' |
    '-' |
    '*' |
    '/' |
    '=' |
    '!=' |
    '<' |
    '>' |
    '<=' |
    '>=' |
    ';' |
    ',' |
    '(' |
    ')'
);

IDENTIFIER : [a-zA-Z] [a-zA-Z0-9]*;

INTLITERAL : ([1-9][0-9]*) | '0';

FLOATLITERAL : INTLITERAL?'.'(([0-9]*[1-9]) | '0');

STRINGLITERAL : '"'(~["])*'"';

WS : (' '|'\t'|'\r'|'\n')+ -> skip ;  // skip spaces, tabs, newlines

//program : IDENTIFIER;

program : 'PROGRAM' id 'BEGIN' pgm_body 'END' ;
id : IDENTIFIER;

pgm_body : decl func_declarations;

decl : string_decl decl | var_decl decl | ;

/* String Declaration */
string_decl : 'STRING' name=id ':=' value=str ';' #StringDeclRule;

str : STRINGLITERAL;

/* Variable Declaration */
var_decl : type=var_type names=id_list';' #VariableDeclRule;
var_type : 'FLOAT' | 'INT';
any_type : var_type | 'VOID';
id_list : id id_tail;

id_tail :',' id id_tail | ;

/* Function Parameter List */
param_decl_list : param_decl param_decl_tail| ;
param_decl : type=var_type name=id #ParameterDeclRule;

param_decl_tail :',' param_decl param_decl_tail| ;

/* Function Declarations */

func_declarations : func_decl func_declarations | ;

func_decl : 'FUNCTION' any_type name=id '('param_decl_list')' 'BEGIN' func_body 'END' #FuncDeclRule;
func_body : decl stmt_list;

/* Statement List */

stmt_list : stmt stmt_list| ;
stmt : basic_stmt | if_stmt | for_stmt;
basic_stmt : assign_stmt | read_stmt | write_stmt | return_stmt;

/* Basic Statements */

assign_stmt : assign_expr ';';
assign_expr : name=id ':=' exp=expr #AssignExprRule;
read_stmt : 'READ''(' names=id_list ')'';' #ReadStmtRule;
write_stmt : 'WRITE''(' names=id_list ')'';' #WriteStmtRule;

return_stmt : 'RETURN' expr';';
/* if_stmt */

if_stmt : 'IF''('con=cond')' dec=decl stm=stmt_list els=else_part 'ENDIF' #IfStmtRule;
else_part : 'ELSE' dec=decl stm=stmt_list #ElseStmtRule | #ElseEpsilonRule;

cond : exp1=expr com=compare exp2=expr #CondRule;

compare :'='|'!='|'<='|'>='|'<'|'>';

/* for_stmt */

for_stmt : 'FOR''(' ini=init_expr ';' con=cond ';' exp=incr_expr ')' dec=decl stm=stmt_list 'ENDFOR' #ForStmtRule;
init_expr : assign_expr | ;
incr_expr : assign_expr | ;

/* Expressions */

expr : pre=expr_prefix ter=term #ExprRule;

expr_prefix : pre=expr_prefix ter=term op=addop #ExprPrefixRule | #ExprPrefixEpsRule;

term : pre=factor_prefix fac=factor #TermRule;

factor_prefix : pre=factor_prefix fac=factor op=mulop #FactorPrefixRule | #FactorPrefixEpsRule;

factor : pri=primary #FactorPrimaryRule | call=call_expr #FactorCallExprRule;

primary : '(' exp=expr ')' #PrimaryExprRule | name=id #PrimaryIDRule | name=INTLITERAL #PrimaryINTRule | name=FLOATLITERAL #PrimaryFLOATRule;
call_expr : id '(' expr_list ')';

expr_list : expr expr_list_tail | ;

expr_list_tail :',' expr expr_list_tail | ;

addop :'+'|'-';

mulop :'*'|'/';