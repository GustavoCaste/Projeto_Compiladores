package parser;

import lexer.Token;

// Erro sintatico. Guarda o contexto (token, submaquina, estado, esperado)
// e monta a mensagem a partir dele.
public class SyntaxException extends Exception {

    private final int line;
    private final String lexeme;
    private final String code;
    private final Submachine submachine;
    private final String state;
    private final String expected;

    public SyntaxException(
            int line,
            String lexeme,
            String code,
            Submachine submachine,
            String state,
            String expected
    ) {
        super(buildMessage(line, lexeme, code, submachine, state, expected));
        this.line = line;
        this.lexeme = lexeme;
        this.code = code;
        this.submachine = submachine;
        this.state = state;
        this.expected = expected;
    }

    // Conveniencia: extrai linha/lexeme/codigo direto do token recebido.
    public SyntaxException(Token token, Submachine submachine, String state, String expected) {
        this(
                token == null ? -1 : token.getLine(),
                token == null ? "<EOF>" : token.getLexeme(),
                token == null ? "-" : token.getCode(),
                submachine,
                state,
                expected
        );
    }

    private static String buildMessage(
            int line,
            String lexeme,
            String code,
            Submachine submachine,
            String state,
            String expected
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("Erro sintatico");
        if (line >= 0) {
            sb.append(" na linha ").append(line);
        }
        sb.append(": lexeme '").append(lexeme == null ? "<EOF>" : lexeme).append('\'');
        sb.append(", codigo ").append(code == null ? "-" : code);
        if (submachine != null) {
            sb.append(", submaquina ").append(submachine);
        }
        if (state != null && !state.isBlank()) {
            sb.append(", estado ").append(state);
        }
        if (expected != null && !expected.isBlank()) {
            sb.append(". Esperado: ").append(expected);
        }
        return sb.append('.').toString();
    }

    public int getLine() {
        return line;
    }

    public String getLexeme() {
        return lexeme;
    }

    public String getCode() {
        return code;
    }

    public Submachine getSubmachine() {
        return submachine;
    }

    public String getState() {
        return state;
    }

    public String getExpected() {
        return expected;
    }
}
