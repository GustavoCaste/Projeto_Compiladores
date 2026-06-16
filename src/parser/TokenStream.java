package parser;

import java.util.ArrayList;
import java.util.List;
import lexer.Token;

// Cursor de leitura sobre a lista de tokens produzida pelo LexicalAnalyzer.
// Nao modifica a lista original: trabalha sobre uma copia imutavel.
public class TokenStream {

    private final List<Token> tokens;
    private int index;

    public TokenStream(List<Token> tokens) {
        // Copia defensiva: a lista original nunca e alterada.
        this.tokens = tokens == null
                ? List.of()
                : List.copyOf(new ArrayList<>(tokens));
        this.index = 0;
    }

    // Ainda ha token nao consumido.
    public boolean hasNext() {
        return index < tokens.size();
    }

    // Token atual sem consumir. Retorna null no fim (sem estourar erro).
    public Token peek() {
        return hasNext() ? tokens.get(index) : null;
    }

    // Consome e retorna o token atual. Retorna null no fim (sem estourar erro).
    public Token next() {
        if (!hasNext()) {
            return null;
        }
        return tokens.get(index++);
    }

    // Posicao atual do cursor.
    public int position() {
        return index;
    }

    // Volta o cursor ao inicio.
    public void reset() {
        index = 0;
    }
}
