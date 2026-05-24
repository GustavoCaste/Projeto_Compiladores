package lexer;

import java.util.ArrayList;
import java.util.List;

import reserved.ReservedTable;
import symboltable.SymbolEntry;
import symboltable.SymbolTable;

// Implementacao inicial do analisador lexico para manter o fluxo funcional.
public class LexicalAnalyzer {

    private final ReservedTable reservedTable;
    private final SymbolTable symbolTable;

    public LexicalAnalyzer(ReservedTable reservedTable, SymbolTable symbolTable) {
        this.reservedTable = reservedTable;
        this.symbolTable = symbolTable;
    }

    public List<Token> analyze(String sourceContent) {
        List<Token> tokens = new ArrayList<>();

        if (sourceContent == null || sourceContent.isBlank()) {
            return tokens;
        }

        // TODO: Implementar a analise lexica completa (comentarios, strings, chars, numeros, simbolos compostos etc).
        // Fluxo provisoriamente simples: quebra por linhas e espacos para demonstrar funcionamento.
        String[] lines = sourceContent.split("\\R", -1);
        for (int i = 0; i < lines.length; i++) {
            int lineNumber = i + 1;
            String line = lines[i];
            if (line.isBlank()) {
                continue;
            }

            String[] chunks = line.trim().split("\\s+");
            for (String chunk : chunks) {
                if (chunk.isBlank()) {
                    continue;
                }

                if (reservedTable.isReserved(chunk)) {
                    String code = reservedTable.getCode(chunk);
                    tokens.add(new Token(chunk, code, -1, lineNumber));
                } else {
                    SymbolEntry entry = symbolTable.addOrGet(chunk, TokenCode.IDENTIFIER, lineNumber);
                    tokens.add(new Token(chunk, TokenCode.IDENTIFIER, entry.getIndex(), lineNumber));
                }
            }
        }

        return tokens;
    }
}
