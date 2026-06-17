package report;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import symboltable.SymbolEntry;

// Gera o arquivo .TAB com cabecalho e listagem da tabela de simbolos.
public class SymbolTableReportWriter {

    public Path write(Path sourceFile, String sourceContent, List<SymbolEntry> symbols) throws IOException {
        Path outputPath = buildOutputPath(sourceFile, ".TAB");

        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            ReportHeader.write(writer, "RELATÓRIO DA TABELA DE SÍMBOLOS", sourceFile);
            writer.write("Conteudo do texto fonte analisado:");
            writer.newLine();
            writer.write(sourceContent == null ? "" : sourceContent);
            writer.newLine();
            writer.write("------------------------------------------------------------");
            writer.newLine();
            writer.write("Entradas:");
            writer.newLine();

            if (symbols == null || symbols.isEmpty()) {
                writer.write("A tabela ainda nao possui simbolos registrados.");
                writer.newLine();
            } else {
                // Calcula a largura maxima de cada coluna (prefixo + valor)
                int maxEntrada  = "Entrada: ".length();
                int maxCodigo   = "Codigo: ".length();
                int maxLexeme   = "Lexeme: ".length();
                int maxAntes    = "QtdCharsAntesTrunc: ".length();
                int maxDepois   = "QtdCharDepoisTrunc: ".length();
                int maxTipo     = "TipoSimb: ".length();

                for (SymbolEntry entry : symbols) {
                    maxEntrada = Math.max(maxEntrada, "Entrada: ".length()           + String.valueOf(entry.getIndex()).length());
                    maxCodigo  = Math.max(maxCodigo,  "Codigo: ".length()            + entry.getCode().length());
                    maxLexeme  = Math.max(maxLexeme,  "Lexeme: ".length()            + entry.getLexeme().length());
                    maxAntes   = Math.max(maxAntes,   "QtdCharsAntesTrunc: ".length() + String.valueOf(entry.getCharsBeforeTrunc()).length());
                    maxDepois  = Math.max(maxDepois,  "QtdCharDepoisTrunc: ".length() + String.valueOf(entry.getCharsAfterTrunc()).length());
                    maxTipo    = Math.max(maxTipo,    "TipoSimb: ".length()           + entry.getSymbolType().length());
                }

                String fmt = "%-" + maxEntrada + "s, %-" + maxCodigo + "s, %-" + maxLexeme
                        + "s, %-" + maxAntes + "s, %-" + maxDepois + "s, %-" + maxTipo + "s, Linhas: (%s).";

                for (SymbolEntry entry : symbols) {
                    String linhas = entry.getLines().stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(", "));

                    String colEntrada = "Entrada: "           + entry.getIndex();
                    String colCodigo  = "Codigo: "            + entry.getCode();
                    String colLexeme  = "Lexeme: "            + entry.getLexeme();
                    String colAntes   = "QtdCharsAntesTrunc: " + entry.getCharsBeforeTrunc();
                    String colDepois  = "QtdCharDepoisTrunc: " + entry.getCharsAfterTrunc();
                    String colTipo    = "TipoSimb: "           + entry.getSymbolType();

                    writer.write(String.format(fmt, colEntrada, colCodigo, colLexeme,
                            colAntes, colDepois, colTipo, linhas));
                    writer.newLine();
                }
            }
        }

        return outputPath;
    }

    private Path buildOutputPath(Path sourceFile, String newExtension) {
        String originalName = sourceFile.getFileName().toString();
        int dotIndex = originalName.lastIndexOf('.');
        String baseName = dotIndex >= 0 ? originalName.substring(0, dotIndex) : originalName;
        return sourceFile.resolveSibling(baseName + newExtension);
    }
}
