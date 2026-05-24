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
        // TODO: Revisar todos os codigos conforme Apendice A oficial da linguagem.
        add("program", "A01");
        add("declarations", "A02");
        add("functions", "A03");
        add("if", "A04");
        add("else", "A05");
        add("while", "A06");
        add("return", "A07");
        add("break", "A08");
        add("print", "A09");
        add("real", TokenCode.REAL);
        add("integer", TokenCode.INTEGER);
        add("string", "A21");
        add("boolean", "A22");
        add("character", "A23");
        add("void", "A24");
        add("true", "A25");
        add("false", "A26");

        add(";", TokenCode.SEMICOLON);
        add(",", "B02");
        add(":", "B03");
        add(":=", TokenCode.ASSIGN);
        add("?", "B05");
        add("(", "B06");
        add(")", "B07");
        add("[", "B08");
        add("]", "B09");
        add("{", "B10");
        add("}", "B11");
        add("+", "B12");
        add("-", "B13");
        add("*", "B14");
        add("/", "B15");
        add("%", "B16");
        add("==", "B17");
        add("!=", "B18");
        add("<", "B19");
        add("<=", "B20");
        add(">", "B21");
        add(">=", "B22");
        add("#", "B23");
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
