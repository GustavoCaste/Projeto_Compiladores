package reserved;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import lexer.TokenCode;

// Tabela de palavras/simbolos reservados e seus codigos.
public class ReservedTable {

    private final Map<String, String> reservedCodes;

    public ReservedTable() {
        this.reservedCodes = new HashMap<>();
        initializeDefaults();
    }

    public boolean isReserved(String lexeme) {
        return reservedCodes.containsKey(normalize(lexeme));
    }

    public String getCode(String lexeme) {
        return reservedCodes.get(normalize(lexeme));
    }

    private void initializeDefaults() {
        add("boolean", TokenCode.BOOLEAN);
        add("break", TokenCode.BREAK);
        add("character", TokenCode.CHARACTER);
        add("declarations", TokenCode.DECLARATIONS);
        add("else", TokenCode.ELSE);
        add("endDeclararions", TokenCode.END_DECLARATIONS); // alias com erro ortografico presente nos arquivos de teste
        add("endDeclarations", TokenCode.END_DECLARATIONS);
        add("endFunction", TokenCode.END_FUNCTION);
        add("endFunctions", TokenCode.END_FUNCTIONS);
        add("endIf", TokenCode.END_IF);
        add("endif", TokenCode.END_IF);
        add("endProgram", TokenCode.END_PROGRAM);
        add("endWhile", TokenCode.END_WHILE);
        add("false", TokenCode.FALSE);
        add("functions", TokenCode.FUNCTIONS);
        add("funcType", TokenCode.FUNC_TYPE);
        add("if", TokenCode.IF);
        add("real", TokenCode.REAL);
        add("integer", TokenCode.INTEGER);
        add("paramType", TokenCode.PARAM_TYPE);
        add("print", TokenCode.PRINT);
        add("program", TokenCode.PROGRAM);
        add("return", TokenCode.RETURN);
        add("string", TokenCode.STRING);
        add("true", TokenCode.TRUE);
        add("varType", TokenCode.VAR_TYPE);
        add("void", TokenCode.VOID);
        add("while", TokenCode.WHILE);

        add(";", TokenCode.SEMICOLON);
        add(",", TokenCode.COMMA);
        add(":", TokenCode.COLON);
        add(":=", TokenCode.ASSIGN);
        add("?", TokenCode.QUESTION);
        add("(", TokenCode.OPEN_PAREN);
        add(")", TokenCode.CLOSE_PAREN);
        add("[", TokenCode.OPEN_BRACKET);
        add("]", TokenCode.CLOSE_BRACKET);
        add("{", TokenCode.OPEN_BRACE);
        add("}", TokenCode.CLOSE_BRACE);
        add("+", TokenCode.PLUS);
        add("-", TokenCode.MINUS);
        add("*", TokenCode.MULTIPLY);
        add("/", TokenCode.DIVIDE);
        add("%", TokenCode.MODULO);
        add("==", TokenCode.EQUAL);
        add("!=", TokenCode.NOT_EQUAL);
        add("#", TokenCode.NOT_EQUAL);
        add("<", TokenCode.LESS);
        add("<=", TokenCode.LESS_EQUAL);
        add(">", TokenCode.GREATER);
        add(">=", TokenCode.GREATER_EQUAL);
    }

    private void add(String lexeme, String code) {
        reservedCodes.put(normalize(lexeme), code);
    }

    private String normalize(String lexeme) {
        if (lexeme == null) {
            return "";
        }
        return lexeme.toLowerCase(Locale.ROOT);
    }
}
