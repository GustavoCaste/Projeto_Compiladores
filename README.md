# Projeto Compiladores - Klebao2026-1 Static Checker

Projeto academico da disciplina Compiladores para construir, em etapas, um Static Checker da linguagem Klebao2026-1.

O repositorio contem a estrutura em Java puro (JDK 17), com:

- Leitura de arquivo-fonte `.261`
- Tabela de palavras e simbolos reservados
- Analisador lexico caractere a caractere
- Tabela de simbolos com truncagem de 30 caracteres
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
  TesteBasico.261
  TestePrograma.261
  TesteCompletoInicial.261
  TesteComentarios.261
  TesteStringsChars.261
  TesteTruncagem.261
```

## Como compilar (PowerShell)

```powershell
javac --release 17 -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName })
```

Sem validar o alvo Java 17 explicitamente:

```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName })
```

## Como executar

Executando com argumento:

```powershell
java -cp out Main examples/MeuTeste
```

O programa adiciona `.261` automaticamente quando a extensao nao for informada. Tambem aceita caminho relativo ou absoluto.

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

- O projeto implementa a etapa lexica e os relatorios exigidos para essa etapa.
- Nao ha Maven, Gradle ou bibliotecas externas.
