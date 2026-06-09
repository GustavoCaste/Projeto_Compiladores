package lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import reserved.ReservedTable;
import symboltable.SymbolEntry;
import symboltable.SymbolTable;

// Analisador lexico simples, dirigido por caracteres, para a linguagem Klebao2026-1.
public class LexicalAnalyzer {

    private static final int MAX_LEXEME_SIZE = 30;

    private final ReservedTable reservedTable;
    private final SymbolTable symbolTable;

    private String source;
    private int index;
    private int line;
    private boolean expectProgramName;
    private FuncContext funcContext;
    private VarContext varContext;
    private String pendingVarType;
    private boolean pendingVarArray;
    private String activeDeclarationType;

    public LexicalAnalyzer(ReservedTable reservedTable, SymbolTable symbolTable) {
        this.reservedTable = reservedTable;
        this.symbolTable = symbolTable;
    }

    public List<Token> analyze(String sourceContent) {
        List<Token> tokens = new ArrayList<>();
        reset(sourceContent);

        while (!isAtEnd()) {
            skipWhitespace();
            if (isAtEnd()) {
                break;
            }

            int tokenLine = line;
            char current = current();

            if (current == '/' && peek() == '/') {
                skipLineComment();
            } else if (current == '/' && peek() == '*') {
                skipBlockComment();
            } else if (current == '"') {
                scanString(tokens, tokenLine);
            } else if (current == '\'') {
                scanChar(tokens, tokenLine);
            } else if (isDigit(current)) {
                scanNumber(tokens, tokenLine);
            } else if (isIdentifierStart(current)) {
                scanWord(tokens, tokenLine);
            } else if (!scanReservedSymbol(tokens, tokenLine)) {
                advance();
            }
        }

        return tokens;
    }

    private void reset(String sourceContent) {
        source = sourceContent == null ? "" : sourceContent;
        index = 0;
        line = 1;
        expectProgramName = false;
        funcContext = FuncContext.NONE;
        resetVarContext();
    }

    private void scanWord(List<Token> tokens, int tokenLine) {
        ValidLexeme lexeme = new ValidLexeme();

        while (!isAtEnd() && !isIdentifierBoundary(current())) {
            char c = current();
            if (isIdentifierPart(c)) {
                lexeme.appendUpper(c);
            }
            advance();
        }

        if (lexeme.isEmpty()) {
            return;
        }

        String text = lexeme.text();
        if (reservedTable.isReserved(text)) {
            String code = reservedTable.getCode(text);
            addPlainToken(tokens, text, code, tokenLine);
            return;
        }

        String code = classifyIdentifier(text);
        String symbolType = TokenCode.VARIABLE.equals(code) && VarContext.IN_DECLARATION.equals(varContext)
                ? activeDeclarationType
                : "-";
        addSymbolToken(tokens, text, code, tokenLine, lexeme.validLength(), symbolType);
    }

    private String classifyIdentifier(String lexeme) {
        if (expectProgramName) {
            expectProgramName = false;
            return isProgramOrFunctionName(lexeme) ? TokenCode.PROGRAM_NAME : TokenCode.VARIABLE;
        }

        if (FuncContext.EXPECT_NAME.equals(funcContext)) {
            funcContext = FuncContext.NONE;
            return isProgramOrFunctionName(lexeme) ? TokenCode.FUNCTION_NAME : TokenCode.VARIABLE;
        }

        return TokenCode.VARIABLE;
    }

    private void scanNumber(List<Token> tokens, int tokenLine) {
        ValidLexeme lexeme = new ValidLexeme();
        readDigitSequence(lexeme);

        boolean real = false;
        if (!isAtEnd() && current() == '.' && hasDigitAt(index + 1)) {
            real = true;
            lexeme.appendRaw('.');
            advance();
            readDigitSequence(lexeme);

            if (hasExponentAt(index)) {
                lexeme.appendRaw('E');
                advance();
                if (!isAtEnd() && (current() == '+' || current() == '-')) {
                    lexeme.appendRaw(current());
                    advance();
                }
                readDigitSequence(lexeme);
            }
        }

        String code = real ? TokenCode.REAL_CONST : TokenCode.INTEGER_CONST;
        addSymbolToken(tokens, lexeme.text(), code, tokenLine, lexeme.validLength(), "-");
    }

    private void readDigitSequence(ValidLexeme lexeme) {
        while (!isAtEnd()) {
            char c = current();
            if (isDigit(c)) {
                lexeme.appendRaw(c);
                advance();
            } else if (isDiscardableInvalidInsideAtom(c)) {
                advance();
            } else {
                break;
            }
        }
    }

    private void scanString(List<Token> tokens, int tokenLine) {
        ValidLexeme lexeme = new ValidLexeme();
        lexeme.appendRaw('"');
        advance();

        boolean closed = false;
        while (!isAtEnd()) {
            char c = current();
            if (c == '"') {
                lexeme.appendRaw('"');
                advance();
                closed = true;
                break;
            }
            if (isLineBreak(c)) {
                break;
            }
            if (isValidStringContent(c)) {
                if (isAsciiLetter(c)) {
                    lexeme.appendUpper(c);
                } else {
                    lexeme.appendRaw(c);
                }
            }
            advance();
        }

        if (closed) {
            addSymbolToken(tokens, lexeme.text(), TokenCode.STRING_CONST, tokenLine, lexeme.validLength(), "-");
        }
    }

    private void scanChar(List<Token> tokens, int tokenLine) {
        ValidLexeme lexeme = new ValidLexeme();
        lexeme.appendRaw('\'');
        advance();

        boolean closed = false;
        int letters = 0;
        while (!isAtEnd()) {
            char c = current();
            if (c == '\'') {
                lexeme.appendRaw('\'');
                advance();
                closed = true;
                break;
            }
            if (isLineBreak(c)) {
                break;
            }
            if (isAsciiLetter(c)) {
                letters++;
                lexeme.appendUpper(c);
            }
            advance();
        }

        if (closed && letters == 1) {
            addSymbolToken(tokens, lexeme.text(), TokenCode.CHAR_CONST, tokenLine, lexeme.validLength(), "-");
        }
    }

    private boolean scanReservedSymbol(List<Token> tokens, int tokenLine) {
        String twoChars = index + 1 < source.length()
                ? "" + current() + source.charAt(index + 1)
                : "";
        if (isTwoCharReserved(twoChars)) {
            advance();
            advance();
            addPlainToken(tokens, twoChars, reservedTable.getCode(twoChars), tokenLine);
            return true;
        }

        String oneChar = String.valueOf(current());
        if (reservedTable.isReserved(oneChar)) {
            advance();
            addPlainToken(tokens, oneChar, reservedTable.getCode(oneChar), tokenLine);
            return true;
        }

        return false;
    }

    private boolean isTwoCharReserved(String lexeme) {
        return ":=".equals(lexeme)
                || "==".equals(lexeme)
                || "!=".equals(lexeme)
                || "<=".equals(lexeme)
                || ">=".equals(lexeme);
    }

    private void addPlainToken(List<Token> tokens, String lexeme, String code, int tokenLine) {
        tokens.add(new Token(lexeme.toUpperCase(Locale.ROOT), code, -1, tokenLine));
        updateContext(code);
    }

    private void addSymbolToken(
            List<Token> tokens,
            String lexeme,
            String code,
            int tokenLine,
            int charsBeforeTrunc,
            String symbolType
    ) {
        SymbolEntry entry = symbolTable.addOrGet(lexeme, code, tokenLine, charsBeforeTrunc, symbolType);
        tokens.add(new Token(entry.getLexeme(), code, entry.getIndex(), tokenLine));
        updateContext(code);
    }

    private void updateContext(String code) {
        updateProgramContext(code);
        updateFunctionContext(code);
        updateVariableDeclarationContext(code);
    }

    private void updateProgramContext(String code) {
        if (TokenCode.PROGRAM.equals(code)) {
            expectProgramName = true;
        }
    }

    private void updateFunctionContext(String code) {
        if (TokenCode.FUNC_TYPE.equals(code)) {
            funcContext = FuncContext.WAIT_TYPE;
            return;
        }

        if (FuncContext.WAIT_TYPE.equals(funcContext)) {
            funcContext = isTypeCode(code) ? FuncContext.WAIT_COLON : FuncContext.NONE;
        } else if (FuncContext.WAIT_COLON.equals(funcContext)) {
            if (TokenCode.COLON.equals(code)) {
                funcContext = FuncContext.EXPECT_NAME;
            } else if (!TokenCode.OPEN_BRACKET.equals(code) && !TokenCode.CLOSE_BRACKET.equals(code)) {
                funcContext = FuncContext.NONE;
            }
        }
    }

    private void updateVariableDeclarationContext(String code) {
        if (TokenCode.VAR_TYPE.equals(code)) {
            varContext = VarContext.WAIT_TYPE;
            pendingVarType = null;
            pendingVarArray = false;
            activeDeclarationType = "-";
            return;
        }

        if (VarContext.WAIT_TYPE.equals(varContext)) {
            if (isTypeCode(code)) {
                pendingVarType = toSymbolType(code, false);
                varContext = VarContext.WAIT_COLON;
            } else {
                resetVarContext();
            }
        } else if (VarContext.WAIT_COLON.equals(varContext)) {
            if (TokenCode.OPEN_BRACKET.equals(code)) {
                pendingVarArray = true;
            } else if (TokenCode.CLOSE_BRACKET.equals(code)) {
                // Mantem o estado ate encontrar ':'.
            } else if (TokenCode.COLON.equals(code)) {
                activeDeclarationType = pendingVarArray
                        ? toArraySymbolType(pendingVarType)
                        : pendingVarType;
                varContext = VarContext.IN_DECLARATION;
            } else {
                resetVarContext();
            }
        } else if (VarContext.IN_DECLARATION.equals(varContext) && TokenCode.SEMICOLON.equals(code)) {
            resetVarContext();
        }
    }

    private void resetVarContext() {
        varContext = VarContext.NONE;
        pendingVarType = null;
        pendingVarArray = false;
        activeDeclarationType = "-";
    }

    private String toSymbolType(String typeCode, boolean array) {
        if (TokenCode.REAL.equals(typeCode)) {
            return array ? "AF" : "FP";
        }
        if (TokenCode.INTEGER.equals(typeCode)) {
            return array ? "AI" : "IN";
        }
        if (TokenCode.STRING.equals(typeCode)) {
            return array ? "AS" : "ST";
        }
        if (TokenCode.CHARACTER.equals(typeCode)) {
            return array ? "AC" : "CH";
        }
        if (TokenCode.BOOLEAN.equals(typeCode)) {
            return array ? "AB" : "BL";
        }
        if (TokenCode.VOID.equals(typeCode)) {
            return "VD";
        }
        return "-";
    }

    private String toArraySymbolType(String scalarType) {
        if ("FP".equals(scalarType)) {
            return "AF";
        }
        if ("IN".equals(scalarType)) {
            return "AI";
        }
        if ("ST".equals(scalarType)) {
            return "AS";
        }
        if ("CH".equals(scalarType)) {
            return "AC";
        }
        if ("BL".equals(scalarType)) {
            return "AB";
        }
        return scalarType == null ? "-" : scalarType;
    }

    private boolean isTypeCode(String code) {
        return TokenCode.REAL.equals(code)
                || TokenCode.INTEGER.equals(code)
                || TokenCode.STRING.equals(code)
                || TokenCode.CHARACTER.equals(code)
                || TokenCode.BOOLEAN.equals(code)
                || TokenCode.VOID.equals(code);
    }

    private boolean isProgramOrFunctionName(String lexeme) {
        if (lexeme == null || lexeme.isBlank() || !isAsciiLetter(lexeme.charAt(0))) {
            return false;
        }
        for (int i = 1; i < lexeme.length(); i++) {
            char c = lexeme.charAt(i);
            if (!isAsciiLetter(c) && !isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    private void skipWhitespace() {
        while (!isAtEnd() && Character.isWhitespace(current())) {
            advance();
        }
    }

    private void skipLineComment() {
        while (!isAtEnd() && !isLineBreak(current())) {
            advance();
        }
    }

    private void skipBlockComment() {
        advance();
        advance();
        while (!isAtEnd()) {
            if (current() == '*' && peek() == '/') {
                advance();
                advance();
                return;
            }
            advance();
        }
    }

    private boolean hasExponentAt(int candidateIndex) {
        if (candidateIndex >= source.length()) {
            return false;
        }

        char e = source.charAt(candidateIndex);
        if (e != 'e' && e != 'E') {
            return false;
        }

        int digitIndex = candidateIndex + 1;
        if (digitIndex < source.length()
                && (source.charAt(digitIndex) == '+' || source.charAt(digitIndex) == '-')) {
            digitIndex++;
        }
        return hasDigitAt(digitIndex);
    }

    private boolean hasDigitAt(int candidateIndex) {
        return candidateIndex < source.length() && isDigit(source.charAt(candidateIndex));
    }

    private boolean isIdentifierStart(char c) {
        return isAsciiLetter(c) || c == '_';
    }

    private boolean isIdentifierPart(char c) {
        return isAsciiLetter(c) || isDigit(c) || c == '_';
    }

    private boolean isIdentifierBoundary(char c) {
        return Character.isWhitespace(c) || c == '"' || c == '\'' || isSymbolStart(c);
    }

    private boolean isDiscardableInvalidInsideAtom(char c) {
        return !Character.isWhitespace(c)
                && c != '"'
                && c != '\''
                && c != '.'
                && !isAsciiLetter(c)
                && !isDigit(c)
                && c != '_'
                && !isSymbolStart(c);
    }

    private boolean isSymbolStart(char c) {
        return c == ';'
                || c == ','
                || c == ':'
                || c == '?'
                || c == '('
                || c == ')'
                || c == '['
                || c == ']'
                || c == '{'
                || c == '}'
                || c == '+'
                || c == '-'
                || c == '*'
                || c == '/'
                || c == '%'
                || c == '='
                || c == '!'
                || c == '#'
                || c == '<'
                || c == '>';
    }

    private boolean isValidStringContent(char c) {
        return isAsciiLetter(c)
                || isDigit(c)
                || c == ' '
                || c == '$'
                || c == '_'
                || c == '.';
    }

    private boolean isAsciiLetter(char c) {
        char upper = Character.toUpperCase(c);
        return upper >= 'A' && upper <= 'Z';
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isLineBreak(char c) {
        return c == '\n' || c == '\r';
    }

    private boolean isAtEnd() {
        return index >= source.length();
    }

    private char current() {
        return source.charAt(index);
    }

    private char peek() {
        return index + 1 < source.length() ? source.charAt(index + 1) : '\0';
    }

    private void advance() {
        if (isAtEnd()) {
            return;
        }

        char c = source.charAt(index);
        if (c == '\r') {
            if (index + 1 < source.length() && source.charAt(index + 1) == '\n') {
                index += 2;
            } else {
                index++;
            }
            line++;
        } else if (c == '\n') {
            index++;
            line++;
        } else {
            index++;
        }
    }

    private static class ValidLexeme {

        private final StringBuilder truncated = new StringBuilder();
        private int validLength;

        void appendUpper(char c) {
            appendRaw(Character.toUpperCase(c));
        }

        void appendRaw(char c) {
            validLength++;
            if (truncated.length() < MAX_LEXEME_SIZE) {
                truncated.append(c);
            }
        }

        boolean isEmpty() {
            return validLength == 0;
        }

        String text() {
            return truncated.toString();
        }

        int validLength() {
            return validLength;
        }
    }

    private enum FuncContext {
        NONE,
        WAIT_TYPE,
        WAIT_COLON,
        EXPECT_NAME
    }

    private enum VarContext {
        NONE,
        WAIT_TYPE,
        WAIT_COLON,
        IN_DECLARATION
    }
}
