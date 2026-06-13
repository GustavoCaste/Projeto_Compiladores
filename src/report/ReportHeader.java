package report;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;

// Escreve o cabeçalho padrão dos relatórios .LEX e .TAB.
public final class ReportHeader {

    private ReportHeader() {
    }

    public static void write(BufferedWriter writer, String reportName, Path sourceFile) throws IOException {
        writer.write("Código da Equipe: EQ07");
        writer.newLine();
        writer.write("Projeto: Static Checker da Linguagem Klebão2026-1");
        writer.newLine();
        writer.write("Relatório: " + reportName);
        writer.newLine();
        writer.write("Arquivo Fonte: " + sourceFile.getFileName());
        writer.newLine();
        writer.newLine();
        writer.write("Integrantes:");
        writer.newLine();
        writer.write("FILIPE MIRANDA DE OLIVEIRA — (71) 99103-1020 — e-mail: __________________");
        writer.newLine();
        writer.write("GUSTAVO CASTELLUCIO DA COSTA LIMA — (71) 99681-5124 — e-mail: __________________");
        writer.newLine();
        writer.write("LEONARDO BRITTO DA SILVA — (71) 99969-0054 — e-mail: __________________");
        writer.newLine();
        writer.write("LUCCA BARBOSA NYGAARD — (71) 99680-1901 — e-mail: __________________");
        writer.newLine();
        writer.newLine();
    }
}