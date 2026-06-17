package symboltable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Representa uma entrada da tabela de simbolos.
public class SymbolEntry {

    private final int index;
    private final String code;
    private final String lexeme;
    private int charsBeforeTrunc;
    private final int charsAfterTrunc;
    private String symbolType;
    private final List<Integer> lines;

    public SymbolEntry(
            int index,
            String code,
            String lexeme,
            int charsBeforeTrunc,
            int charsAfterTrunc,
            String symbolType,
            int firstLine
    ) {
        this.index = index;
        this.code = code;
        this.lexeme = lexeme;
        this.charsBeforeTrunc = charsBeforeTrunc;
        this.charsAfterTrunc = charsAfterTrunc;
        this.symbolType = symbolType;
        this.lines = new ArrayList<>();
        addLine(firstLine);
    }

    public int getIndex() {
        return index;
    }

    public String getCode() {
        return code;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getCharsBeforeTrunc() {
        return charsBeforeTrunc;
    }

    public int getCharsAfterTrunc() {
        return charsAfterTrunc;
    }

    public String getSymbolType() {
        return symbolType;
    }

    public void updateSymbolType(String newSymbolType) {
        if (newSymbolType == null || newSymbolType.isBlank() || "-".equals(newSymbolType)) {
            return;
        }
        if (symbolType == null || symbolType.isBlank() || "-".equals(symbolType)) {
            symbolType = newSymbolType;
        }
    }

    // Atualiza para o maior valor visto entre aparicoes (sem contar invalidos; aspas contam).
    public void updateCharsBeforeTrunc(int newValue) {
        if (newValue > charsBeforeTrunc) {
            charsBeforeTrunc = newValue;
        }
    }

    public List<Integer> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public void addLine(int line) {
        if (line <= 0) {
            return;
        }
        if (lines.size() >= 5) {
            return;
        }
        lines.add(line);
    }

    @Override
    public String toString() {
        return "SymbolEntry{"
                + "index=" + index
                + ", code='" + code + '\''
                + ", lexeme='" + lexeme + '\''
                + ", charsBeforeTrunc=" + charsBeforeTrunc
                + ", charsAfterTrunc=" + charsAfterTrunc
                + ", symbolType='" + symbolType + '\''
                + ", lines=" + lines
                + '}';
    }
}
