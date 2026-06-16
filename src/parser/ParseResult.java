package parser;

// Resultado da analise sintatica: sucesso ou falha com mensagem.
public class ParseResult {

    private final boolean success;
    private final String message;

    private ParseResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static ParseResult success() {
        return new ParseResult(true, "Analise sintatica concluida com sucesso.");
    }

    public static ParseResult failure(String message) {
        return new ParseResult(false, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
