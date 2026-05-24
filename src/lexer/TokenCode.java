package lexer;

// Centraliza codigos iniciais de tokens. TODO: completar com todos os codigos oficiais do Apendice A.
public final class TokenCode {

    public static final String IDENTIFIER = "C01";
    public static final String PROGRAM_NAME = "C03";
    public static final String INTEGER_CONST = "C06";
    public static final String REAL_CONST = "C07";
    public static final String SEMICOLON = "B01";
    public static final String ASSIGN = "B04";
    public static final String INTEGER = "A16";
    public static final String REAL = "A20";

    private TokenCode() {
    }
}
