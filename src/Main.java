import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import lexer.LexicalAnalyzer;
import lexer.Token;
import report.LexReportWriter;
import report.SymbolTableReportWriter;
import reserved.ReservedTable;
import symboltable.SymbolTable;
import util.SourceReader;

// Classe de entrada do projeto: le arquivo .261 e executa o fluxo inicial.
public class Main {

    public static void main(String[] args) {
        try {
            String userInput = args.length > 0 ? args[0] : requestFileNameFromConsole();
            if (userInput == null || userInput.isBlank()) {
                System.err.println("Nenhum arquivo informado.");
                return;
            }

            SourceReader sourceReader = new SourceReader();
            Path sourceFile = sourceReader.locateSourceFile(userInput);
            System.out.println("Arquivo localizado: " + sourceFile);

            String sourceContent = sourceReader.readAllContent(sourceFile);
            System.out.println("Leitura do arquivo concluida.");

            ReservedTable reservedTable = new ReservedTable();
            SymbolTable symbolTable = new SymbolTable();
            LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(reservedTable, symbolTable);

            List<Token> tokens = lexicalAnalyzer.analyze(sourceContent);
            System.out.println("Analise lexica executada. Tokens: " + tokens.size());

            LexReportWriter lexReportWriter = new LexReportWriter();
            Path lexPath = lexReportWriter.write(sourceFile, sourceContent, tokens);

            SymbolTableReportWriter tabWriter = new SymbolTableReportWriter();
            Path tabPath = tabWriter.write(sourceFile, sourceContent, symbolTable.getAll());

            System.out.println("Relatorios gerados com sucesso:");
            System.out.println(" - " + lexPath);
            System.out.println(" - " + tabPath);
        } catch (Exception e) {
            System.err.println("Erro ao executar o fluxo inicial: " + e.getMessage());
            System.exit(1);
        }
    }

    private static String requestFileNameFromConsole() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Informe o nome base ou caminho do arquivo .261: ");
        return scanner.nextLine();
    }
}
