package lexer;

// Centraliza os codigos oficiais usados pelo analisador lexico.
public final class TokenCode {

    public static final String VARIABLE = "C01";
    public static final String FUNCTION_NAME = "C02";
    public static final String PROGRAM_NAME = "C03";
    public static final String STRING_CONST = "C04";
    public static final String CHAR_CONST = "C05";
    public static final String INTEGER_CONST = "C06";
    public static final String REAL_CONST = "C07";

    public static final String BOOLEAN = "A01";
    public static final String BREAK = "A02";
    public static final String CHARACTER = "A03";
    public static final String DECLARATIONS = "A04";
    public static final String ELSE = "A05";
    public static final String END_DECLARATIONS = "A06";
    public static final String END_FUNCTION = "A07";
    public static final String END_FUNCTIONS = "A08";
    public static final String END_IF = "A09";
    public static final String END_PROGRAM = "A10";
    public static final String END_WHILE = "A11";
    public static final String FALSE = "A12";
    public static final String FUNCTIONS = "A13";
    public static final String FUNC_TYPE = "A14";
    public static final String IF = "A15";
    public static final String INTEGER = "A16";
    public static final String PARAM_TYPE = "A17";
    public static final String PRINT = "A18";
    public static final String PROGRAM = "A19";
    public static final String REAL = "A20";
    public static final String RETURN = "A21";
    public static final String STRING = "A22";
    public static final String TRUE = "A23";
    public static final String VAR_TYPE = "A24";
    public static final String VOID = "A25";
    public static final String WHILE = "A26";

    public static final String SEMICOLON = "B01";
    public static final String COMMA = "B02";
    public static final String COLON = "B03";
    public static final String ASSIGN = "B04";
    public static final String QUESTION = "B05";
    public static final String OPEN_PAREN = "B06";
    public static final String CLOSE_PAREN = "B07";
    public static final String OPEN_BRACKET = "B08";
    public static final String CLOSE_BRACKET = "B09";
    public static final String OPEN_BRACE = "B10";
    public static final String CLOSE_BRACE = "B11";
    public static final String PLUS = "B12";
    public static final String MINUS = "B13";
    public static final String MULTIPLY = "B14";
    public static final String DIVIDE = "B15";
    public static final String MODULO = "B16";
    public static final String EQUAL = "B17";
    public static final String NOT_EQUAL = "B18";
    public static final String LESS = "B19";
    public static final String LESS_EQUAL = "B20";
    public static final String GREATER = "B21";
    public static final String GREATER_EQUAL = "B22";

    private TokenCode() {
    }
}
