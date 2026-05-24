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
            writer.write("Codigo da Equipe: E__");
            writer.newLine();
            writer.write("Componentes: ");
            writer.newLine();
            writer.write("RELATORIO DA TABELA DE SIMBOLOS");
            writer.newLine();
            writer.newLine();
            writer.write("Texto fonte analisado:");
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
                writer.write(String.format(
                        "%-6s %-8s %-20s %-8s %-8s %-12s %-20s",
                        "IDX",
                        "CODIGO",
                        "LEXEME",
                        "ANTES",
                        "DEPOIS",
                        "TIPO",
                        "LINHAS"
                ));
                writer.newLine();

                for (SymbolEntry entry : symbols) {
                    String lines = entry.getLines().stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(","));
                    writer.write(String.format(
                            "%-6d %-8s %-20s %-8d %-8d %-12s %-20s",
                            entry.getIndex(),
                            entry.getCode(),
                            entry.getLexeme(),
                            entry.getCharsBeforeTrunc(),
                            entry.getCharsAfterTrunc(),
                            entry.getSymbolType(),
                            lines
                    ));
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
