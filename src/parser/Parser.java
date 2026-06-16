package parser;

import lexer.Token;
import lexer.TokenCode;

// Analisador sintatico descendente recursivo para Klebao2026-1.
// Consome os tokens via TokenStream. Faz apenas validacao sintatica
// (sem semantica). Organizado nas 4 submaquinas do projeto:
//   SUB01 FileProgram, SUB02 Command, SUB03 LogicalExp, SUB04 AddExp.
//
// A comparacao e baseada no CODIGO do token (TokenCode), nao no lexeme,
// porque o lexer ja entrega o lexeme em caixa alta. matchLexeme existe
// como apoio e e case insensitive.
public class Parser {

    private final TokenStream tokens;
    private Submachine sub;

    public Parser(TokenStream tokens) {
        this.tokens = tokens;
    }

    public ParseResult parse() {
        try {
            tokens.reset();
            parseFileProgram();

            if (tokens.hasNext()) {
                Token t = tokens.peek();
                return ParseResult.failure(
                        "Erro sintatico na linha " + t.getLine()
                                + ": token inesperado " + t.getLexeme()
                                + " (" + t.getCode() + ") apos o fim do programa."
                                + " Esperado: fim do arquivo.");
            }
            return ParseResult.success();
        } catch (SyntaxException e) {
            return ParseResult.failure(e.getMessage());
        }
    }

    // ===================== SUB01 - FileProgram =====================

    private void parseFileProgram() throws SyntaxException {
        sub = Submachine.SUB01_FILE_PROGRAM;

        expectCode(TokenCode.PROGRAM, "inicio", "program");
        expectCode(TokenCode.PROGRAM_NAME, "nome-programa", "nome do programa");
        expectCode(TokenCode.DECLARATIONS, "declarations", "declarations");

        parseDeclarations();
        expectCode(TokenCode.END_DECLARATIONS, "fim-declaracoes", "endDeclararions");

        if (matchCode(TokenCode.FUNCTIONS)) {
            advance();
            parseFunctions();
            expectCode(TokenCode.END_FUNCTIONS, "fim-funcoes", "endFunctions");
        }

        expectCode(TokenCode.END_PROGRAM, "fim-programa", "endProgram");
    }

    // Pelo menos uma declaracao varType. O ';' funciona como separador e e
    // tolerado tambem como terminador antes de endDeclararions.
    private void parseDeclarations() throws SyntaxException {
        if (!matchCode(TokenCode.VAR_TYPE)) {
            error("declaracao", "varType (ao menos uma declaracao)");
        }
        while (matchCode(TokenCode.VAR_TYPE)) {
            parseOneVarDecl();
            if (matchCode(TokenCode.SEMICOLON)) {
                advance();
            }
        }
    }

    // varType TIPO [ '[' ']' ] : variable [ '[' intConst ']' ] { , variable [ '[' intConst ']' ] }
    private void parseOneVarDecl() throws SyntaxException {
        expectCode(TokenCode.VAR_TYPE, "varType", "varType");
        if (!matchTipo()) {
            error("tipo", "tipo (integer, real, string, character, boolean, void)");
        }
        advance();

        if (matchCode(TokenCode.OPEN_BRACKET)) {
            advance();
            expectCode(TokenCode.CLOSE_BRACKET, "vetor", "]");
        }

        expectCode(TokenCode.COLON, "dois-pontos", ":");

        do {
            expectCode(TokenCode.VARIABLE, "variavel", "identificador de variavel");
            if (matchCode(TokenCode.OPEN_BRACKET)) {
                advance();
                expectCode(TokenCode.INTEGER_CONST, "indice", "constante inteira");
                expectCode(TokenCode.CLOSE_BRACKET, "indice", "]");
            }
            if (matchCode(TokenCode.COMMA)) {
                advance();
            } else {
                break;
            }
        } while (true);
    }

    // Uma ou mais funcoes separadas por ';'. Um ';' final antes de
    // endFunctions e tolerado.
    private void parseFunctions() throws SyntaxException {
        parseFunction();
        while (matchCode(TokenCode.SEMICOLON)) {
            advance();
            if (matchCode(TokenCode.FUNC_TYPE)) {
                parseFunction();
            } else {
                break;
            }
        }
    }

    // funcType TIPO : functionName ( params ) Command endFunction
    private void parseFunction() throws SyntaxException {
        sub = Submachine.SUB01_FILE_PROGRAM;
        expectCode(TokenCode.FUNC_TYPE, "funcType", "funcType");
        if (!matchTipo()) {
            error("tipo-funcao", "tipo de retorno");
        }
        advance();
        expectCode(TokenCode.COLON, "dois-pontos-funcao", ":");
        expectCode(TokenCode.FUNCTION_NAME, "nome-funcao", "nome da funcao");
        expectCode(TokenCode.OPEN_PAREN, "abre-parenteses", "(");
        parseParams();
        expectCode(TokenCode.CLOSE_PAREN, "fecha-parenteses", ")");
        parseCommand();
        expectCode(TokenCode.END_FUNCTION, "fim-funcao", "endFunction");
    }

    // '?' OU grupos paramType separados por ';'.
    private void parseParams() throws SyntaxException {
        if (matchCode(TokenCode.QUESTION)) {
            advance();
            return;
        }
        parseParamGroup();
        while (matchCode(TokenCode.SEMICOLON)) {
            advance();
            parseParamGroup();
        }
    }

    // paramType TIPO : variable [ '[' [intConst] ']' ] { , variable [ '[' [intConst] ']' ] }
    private void parseParamGroup() throws SyntaxException {
        expectCode(TokenCode.PARAM_TYPE, "paramType", "paramType");
        if (!matchTipo()) {
            error("tipo-param", "tipo do parametro");
        }
        advance();
        expectCode(TokenCode.COLON, "dois-pontos-param", ":");

        do {
            expectCode(TokenCode.VARIABLE, "variavel-param", "identificador de variavel");
            if (matchCode(TokenCode.OPEN_BRACKET)) {
                advance();
                if (matchCode(TokenCode.INTEGER_CONST)) {
                    advance();
                }
                expectCode(TokenCode.CLOSE_BRACKET, "vetor-param", "]");
            }
            if (matchCode(TokenCode.COMMA)) {
                advance();
            } else {
                break;
            }
        } while (true);
    }

    // ===================== SUB02 - Command =====================

    private void parseCommand() throws SyntaxException {
        sub = Submachine.SUB02_COMMAND;
        Token t = tokens.peek();
        if (t == null) {
            error("comando", "um comando");
        }

        String code = t.getCode();

        if (TokenCode.OPEN_BRACE.equals(code)) {
            // bloco: { Command { ; Command } }
            advance();
            parseCommand();
            while (matchCode(TokenCode.SEMICOLON)) {
                advance();
                parseCommand();
            }
            sub = Submachine.SUB02_COMMAND;
            expectCode(TokenCode.CLOSE_BRACE, "fim-bloco", "} ou ;");
        } else if (TokenCode.VARIABLE.equals(code)) {
            // atribuicao: variable [ '[' intConst ']' ] := LogicalExp
            advance();
            if (matchCode(TokenCode.OPEN_BRACKET)) {
                advance();
                expectCode(TokenCode.INTEGER_CONST, "indice", "constante inteira");
                expectCode(TokenCode.CLOSE_BRACKET, "indice", "]");
            }
            expectCode(TokenCode.ASSIGN, "atribuicao", ":=");
            parseLogicalExp();
        } else if (TokenCode.PRINT.equals(code)) {
            advance();
            parseAddExp();
        } else if (TokenCode.IF.equals(code)) {
            advance();
            expectCode(TokenCode.OPEN_PAREN, "if-abre", "(");
            parseLogicalExp();
            sub = Submachine.SUB02_COMMAND;
            expectCode(TokenCode.CLOSE_PAREN, "if-fecha", ")");
            parseCommand();
            sub = Submachine.SUB02_COMMAND;
            if (matchCode(TokenCode.ELSE)) {
                advance();
                parseCommand();
            }
            sub = Submachine.SUB02_COMMAND;
            expectCode(TokenCode.END_IF, "fim-if", "endIf ou else");
        } else if (TokenCode.WHILE.equals(code)) {
            advance();
            expectCode(TokenCode.OPEN_PAREN, "while-abre", "(");
            parseLogicalExp();
            sub = Submachine.SUB02_COMMAND;
            expectCode(TokenCode.CLOSE_PAREN, "while-fecha", ")");
            parseCommand();
            sub = Submachine.SUB02_COMMAND;
            expectCode(TokenCode.END_WHILE, "fim-while", "endWhile");
        } else if (TokenCode.RETURN.equals(code)) {
            advance();
            if (startsAddExp()) {
                parseAddExp();
            }
        } else if (TokenCode.BREAK.equals(code)) {
            advance();
        } else {
            error("comando", "comando (atribuicao, print, if, while, return, break ou '{')");
        }
    }

    // ===================== SUB03 - LogicalExp =====================
    // AddExp { RELOP AddExp }. O agrupamento por parenteses e tratado
    // dentro de AddExp (fator entre parenteses), evitando ambiguidade.

    private void parseLogicalExp() throws SyntaxException {
        sub = Submachine.SUB03_LOGICAL_EXP;
        parseAddExp();
        while (matchRelop()) {
            advance();
            parseAddExp();
        }
    }

    // ===================== SUB04 - AddExp =====================
    // AddExp  = Term { ('+'|'-') Term }
    // Term    = Factor { ('*'|'/'|'%') Factor }
    // Factor  = { '-' } Primary
    // Primary = '(' AddExp ')' | variable [ '[' intConst ']' ] | const

    private void parseAddExp() throws SyntaxException {
        sub = Submachine.SUB04_ADD_EXP;
        parseTerm();
        while (matchOpAdd()) {
            advance();
            parseTerm();
        }
    }

    private void parseTerm() throws SyntaxException {
        parseFactor();
        while (matchOpMult()) {
            advance();
            parseFactor();
        }
    }

    private void parseFactor() throws SyntaxException {
        while (matchCode(TokenCode.MINUS)) {
            advance();
        }
        parsePrimary();
    }

    private void parsePrimary() throws SyntaxException {
        if (matchCode(TokenCode.OPEN_PAREN)) {
            advance();
            parseAddExp();
            expectCode(TokenCode.CLOSE_PAREN, "primario-fecha", ")");
        } else if (matchCode(TokenCode.VARIABLE)) {
            advance();
            if (matchCode(TokenCode.OPEN_BRACKET)) {
                advance();
                expectCode(TokenCode.INTEGER_CONST, "indice", "constante inteira");
                expectCode(TokenCode.CLOSE_BRACKET, "indice", "]");
            }
        } else if (matchConst()) {
            advance();
        } else {
            error("primario", "variavel, constante ou '('");
        }
    }

    // ===================== Auxiliares de token =====================

    private boolean matchCode(String code) {
        Token t = tokens.peek();
        return t != null && t.getCode().equals(code);
    }

    private boolean matchLexeme(String lexeme) {
        Token t = tokens.peek();
        return t != null && t.getLexeme().equalsIgnoreCase(lexeme);
    }

    private boolean matchTipo() {
        return matchCode(TokenCode.INTEGER)
                || matchCode(TokenCode.REAL)
                || matchCode(TokenCode.STRING)
                || matchCode(TokenCode.CHARACTER)
                || matchCode(TokenCode.BOOLEAN)
                || matchCode(TokenCode.VOID);
    }

    private boolean matchConst() {
        return matchCode(TokenCode.INTEGER_CONST)
                || matchCode(TokenCode.REAL_CONST)
                || matchCode(TokenCode.STRING_CONST)
                || matchCode(TokenCode.CHAR_CONST)
                || matchCode(TokenCode.TRUE)
                || matchCode(TokenCode.FALSE);
    }

    private boolean matchRelop() {
        return matchCode(TokenCode.LESS)
                || matchCode(TokenCode.LESS_EQUAL)
                || matchCode(TokenCode.GREATER)
                || matchCode(TokenCode.GREATER_EQUAL)
                || matchCode(TokenCode.EQUAL)
                || matchCode(TokenCode.NOT_EQUAL);
    }

    private boolean matchOpMult() {
        return matchCode(TokenCode.MULTIPLY)
                || matchCode(TokenCode.DIVIDE)
                || matchCode(TokenCode.MODULO);
    }

    private boolean matchOpAdd() {
        return matchCode(TokenCode.PLUS) || matchCode(TokenCode.MINUS);
    }

    private boolean startsAddExp() {
        return matchCode(TokenCode.MINUS)
                || matchCode(TokenCode.OPEN_PAREN)
                || matchCode(TokenCode.VARIABLE)
                || matchConst();
    }

    private Token advance() {
        return tokens.next();
    }

    private Token expectCode(String code, String state, String expected) throws SyntaxException {
        if (matchCode(code)) {
            return advance();
        }
        throw new SyntaxException(tokens.peek(), sub, state, expected);
    }

    private Token expectLexeme(String lexeme, String state, String expected) throws SyntaxException {
        if (matchLexeme(lexeme)) {
            return advance();
        }
        throw new SyntaxException(tokens.peek(), sub, state, expected);
    }

    private void error(String state, String expected) throws SyntaxException {
        throw new SyntaxException(tokens.peek(), sub, state, expected);
    }
}
