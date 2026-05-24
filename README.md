# Projeto Compiladores - Klebao2026-1 Static Checker

Projeto academico da disciplina Compiladores para construir, em etapas, um Static Checker da linguagem Klebao2026-1.

Neste primeiro passo, o repositorio contem a estrutura inicial em Java puro (JDK 17), com:

- Leitura de arquivo-fonte `.261`
- Inicializacao de tabelas basicas
- Fluxo inicial do analisador lexico (ainda incompleto)
- Geracao de relatorios `.LEX` e `.TAB`

## Tecnologias

- Java 17 (OpenJDK 17 LTS)
- `javac` e `java` via terminal
- Sem Maven
- Sem Gradle
- Sem bibliotecas externas

## Estrutura de pastas

```text
src/
  Main.java
  lexer/
    LexicalAnalyzer.java
    Token.java
    TokenCode.java
  symboltable/
    SymbolEntry.java
    SymbolTable.java
  reserved/
    ReservedTable.java
  report/
    LexReportWriter.java
    SymbolTableReportWriter.java
  util/
    SourceReader.java

examples/
  MeuTeste.261
```

## Como compilar (PowerShell)

```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName })
```

Comando alternativo (quando suportado no ambiente):

```powershell
javac -d out (Get-ChildItem -Recurse src/*.java)
```

## Como executar

Executando com argumento:

```powershell
java -cp out Main examples/MeuTeste
```

Ou sem argumento (o programa pedira no console):

```powershell
java -cp out Main
```

## Saida esperada

Ao analisar `examples/MeuTeste.261`, o programa gera:

- `examples/MeuTeste.LEX`
- `examples/MeuTeste.TAB`

Os dois arquivos sao criados na mesma pasta do arquivo `.261` informado.

## Observacoes

- A analise lexica completa ainda nao esta implementada.
- Ha `TODO`s no codigo para as proximas etapas (regras lexicas, codigos oficiais, validacoes adicionais).
