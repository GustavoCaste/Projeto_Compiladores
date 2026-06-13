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
            ReportHeader.write(writer, "Tabela de Símbolos (.TAB)", sourceFile);
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
                for (SymbolEntry entry : symbols) {
                    String lines = entry.getLines().stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(","));
                    writer.write("Entrada: " + entry.getIndex()
                            + ", Codigo: " + entry.getCode()
                            + ", Lexeme: " + entry.getLexeme()
                            + ", QtdCharsAntesTrunc: " + entry.getCharsBeforeTrunc()
                            + ", QtdCharDepoisTrunc: " + entry.getCharsAfterTrunc()
                            + ", TipoSimb: " + entry.getSymbolType()
                            + ", Linhas: (" + lines + ").");
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
