package symboltable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// Estrutura simples de tabela de simbolos com deduplicacao por lexema.
public class SymbolTable {

    private static final int LEXEME_MAX_SIZE = 30;

    private final List<SymbolEntry> entries;
    private final Map<String, SymbolEntry> indexByLexeme;

    public SymbolTable() {
        this.entries = new ArrayList<>();
        this.indexByLexeme = new HashMap<>();
    }

    public SymbolEntry addOrGet(String lexeme, String code, int line) {
        return addOrGet(lexeme, code, line, safeLength(lexeme), "-");
    }

    public SymbolEntry addOrGet(String lexeme, String code, int line, int charsBeforeTrunc, String symbolType) {
        String truncatedLexeme = truncateLexeme(lexeme);
        int safeCharsBeforeTrunc = Math.max(charsBeforeTrunc, truncatedLexeme.length());
        String key = buildKey(truncatedLexeme, code);

        SymbolEntry existing = indexByLexeme.get(key);
        if (existing != null) {
            existing.addLine(line);
            existing.updateSymbolType(symbolType);
            existing.updateCharsBeforeTrunc(safeCharsBeforeTrunc);
            return existing;
        }

        int index = entries.size() + 1;
        SymbolEntry created = new SymbolEntry(
                index,
                code,
                truncatedLexeme,
                safeCharsBeforeTrunc,
                truncatedLexeme.length(),
                normalizeSymbolType(symbolType),
                line
        );
        entries.add(created);
        indexByLexeme.put(key, created);
        return created;
    }

    public SymbolEntry findByLexeme(String lexeme) {
        String truncatedLexeme = truncateLexeme(lexeme);
        String normalizedLexeme = normalizeKey(truncatedLexeme);
        for (Map.Entry<String, SymbolEntry> entry : indexByLexeme.entrySet()) {
            if (entry.getKey().endsWith("|" + normalizedLexeme)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public List<SymbolEntry> getAll() {
        return Collections.unmodifiableList(entries);
    }

    private String truncateLexeme(String lexeme) {
        if (lexeme == null) {
            return "";
        }
        if (lexeme.length() <= LEXEME_MAX_SIZE) {
            return lexeme;
        }
        return lexeme.substring(0, LEXEME_MAX_SIZE);
    }

    private int safeLength(String lexeme) {
        return lexeme == null ? 0 : lexeme.length();
    }

    private String buildKey(String lexeme, String code) {
        return (code == null ? "" : code) + "|" + normalizeKey(lexeme);
    }

    private String normalizeKey(String lexeme) {
        return lexeme.toUpperCase(Locale.ROOT);
    }

    private String normalizeSymbolType(String symbolType) {
        if (symbolType == null || symbolType.isBlank()) {
            return "-";
        }
        return symbolType;
    }
}
