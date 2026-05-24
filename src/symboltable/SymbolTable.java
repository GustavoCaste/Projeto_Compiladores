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
        String truncatedLexeme = truncateLexeme(lexeme);
        String key = normalizeKey(truncatedLexeme);

        SymbolEntry existing = indexByLexeme.get(key);
        if (existing != null) {
            existing.addLine(line);
            return existing;
        }

        int index = entries.size() + 1;
        SymbolEntry created = new SymbolEntry(
                index,
                code,
                truncatedLexeme,
                lexeme.length(),
                truncatedLexeme.length(),
                "-",
                line
        );
        entries.add(created);
        indexByLexeme.put(key, created);
        return created;
    }

    public SymbolEntry findByLexeme(String lexeme) {
        String truncatedLexeme = truncateLexeme(lexeme);
        return indexByLexeme.get(normalizeKey(truncatedLexeme));
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
        // TODO: Reforcar regra oficial de truncamento apos implementar validacao completa de atomos.
        return lexeme.substring(0, LEXEME_MAX_SIZE);
    }

    private String normalizeKey(String lexeme) {
        return lexeme.toUpperCase(Locale.ROOT);
    }
}
