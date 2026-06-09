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
            writer.write("Codigo da Equipe: E__");
            writer.newLine();
            writer.write("Componentes: ");
            writer.newLine();
            writer.write("Contatos: ");
            writer.newLine();
            writer.write("RELATORIO DA ANALISE LEXICA");
            writer.newLine();
            writer.newLine();
            writer.write("Nome do texto fonte analisado: " + sourceFile.getFileName());
            writer.newLine();
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
                for (Token token : tokens) {
                    writer.write("Lexeme: " + token.getLexeme()
                            + ", Codigo: " + token.getCode()
                            + ", indiceTabSimb: " + formatSymbolTableIndex(token.getSymbolTableIndex())
                            + ", Linha: " + token.getLine()
                            + ".");
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
