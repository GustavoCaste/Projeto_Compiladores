package lexer;

// Representa um atomo encontrado durante a analise lexica.
public class Token {

    private final String lexeme;
    private final String code;
    private final int symbolTableIndex;
    private final int line;

    public Token(String lexeme, String code, int symbolTableIndex, int line) {
        this.lexeme = lexeme;
        this.code = code;
        this.symbolTableIndex = symbolTableIndex;
        this.line = line;
    }

    public String getLexeme() {
        return lexeme;
    }

    public String getCode() {
        return code;
    }

    public int getSymbolTableIndex() {
        return symbolTableIndex;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        return "Token{"
                + "lexeme='" + lexeme + '\''
                + ", code='" + code + '\''
                + ", symbolTableIndex=" + symbolTableIndex
                + ", line=" + line
                + '}';
    }
}
