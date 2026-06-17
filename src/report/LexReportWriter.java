package report;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lexer.Token;

// Gera o arquivo .LEX com cabecalho e listagem de tokens.
public class LexReportWriter {

    public Path write(Path sourceFile, String sourceContent, List<Token> tokens) throws IOException {
        Path outputPath = buildOutputPath(sourceFile, ".LEX");

        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            ReportHeader.write(writer, "RELATÓRIO DA ANÁLISE LÉXICA", sourceFile);
            writer.write("Conteudo do texto fonte analisado:");
            writer.newLine();
            writer.write(sourceContent == null ? "" : sourceContent);
            writer.newLine();
            writer.write("------------------------------------------------------------");
            writer.newLine();
            writer.write("Tokens:");
            writer.newLine();

            if (tokens == null || tokens.isEmpty()) {
                writer.write("Nenhum token encontrado.");
                writer.newLine();
            } else {
                int maxLexeme = "Lexeme: ".length();
                int maxCodigo = "Codigo: ".length();
                int maxIndice = "indiceTabSimb: ".length();

                for (Token token : tokens) {
                    maxLexeme = Math.max(maxLexeme, "Lexeme: ".length()        + token.getLexeme().length());
                    maxCodigo = Math.max(maxCodigo, "Codigo: ".length()        + token.getCode().length());
                    maxIndice = Math.max(maxIndice, "indiceTabSimb: ".length() + formatSymbolTableIndex(token.getSymbolTableIndex()).length());
                }

                String fmt = "%-" + maxLexeme + "s, %-" + maxCodigo + "s, %-" + maxIndice + "s, Linha: %s.";

                for (Token token : tokens) {
                    String colLexeme = "Lexeme: "        + token.getLexeme();
                    String colCodigo = "Codigo: "        + token.getCode();
                    String colIndice = "indiceTabSimb: " + formatSymbolTableIndex(token.getSymbolTableIndex());

                    writer.write(String.format(fmt, colLexeme, colCodigo, colIndice, token.getLine()));
                    writer.newLine();
                }
            }
        }

        return outputPath;
    }

    private String formatSymbolTableIndex(int symbolTableIndex) {
        return symbolTableIndex < 0 ? "-" : String.valueOf(symbolTableIndex);
    }

    private Path buildOutputPath(Path sourceFile, String newExtension) {
        String originalName = sourceFile.getFileName().toString();
        int dotIndex = originalName.lastIndexOf('.');
        String baseName = dotIndex >= 0 ? originalName.substring(0, dotIndex) : originalName;
        return sourceFile.resolveSibling(baseName + newExtension);
    }
}
