# Projeto Compiladores - Static Checker Klebão2026-1

Este repositório contém a implementação do projeto final da disciplina de Compiladores, desenvolvido em Java puro. O objetivo principal é construir um Static Checker para a linguagem Klebão2026-1, realizando a análise léxica, a geração da tabela de símbolos, os relatórios exigidos e a validação sintática dos arquivos de entrada.

## Tecnologias

- Java 17 ou superior
- Compilação com `javac`
- Execução com `java`
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

  parser/
    Parser.java
    ParseResult.java
    Submachine.java
    SyntaxException.java
    TokenStream.java

  reserved/
    ReservedTable.java

  symboltable/
    SymbolEntry.java
    SymbolTable.java

  report/
    LexReportWriter.java
    ReportHeader.java
    SymbolTableReportWriter.java

  util/
    SourceReader.java

examples/
  arquivos de teste .261
```

## Funcionalidades Implementadas

- Leitura de arquivo `.261`
- Análise léxica caractere a caractere
- Reconhecimento de palavras reservadas
- Reconhecimento de símbolos reservados
- Reconhecimento de identificadores e constantes
- Comentários de linha `//`
- Comentários de bloco `/* */`
- Controle de linha
- Truncagem de lexemes em 30 caracteres válidos
- Tabela de símbolos
- Geração de relatório `.LEX`
- Geração de relatório `.TAB`
- Análise sintática
- Mensagem de sucesso ou erro sintático controlado

## Como compilar no PowerShell

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac --release 17 -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName })
```

## Como compilar no Linux/Git Bash

```bash
mkdir -p out
javac --release 17 -encoding UTF-8 -d out $(find src -name "*.java")
```

## Como executar pelo código compilado

Exemplo com arquivo sintaticamente válido, sem informar a extensão:

```powershell
java -cp out Main examples\TesteSintaticoValido
```

Exemplo informando a extensão:

```powershell
java -cp out Main examples\TesteSintaticoValido.261
```

O programa adiciona `.261` automaticamente quando a extensão não é informada. Também aceita caminho relativo ou absoluto.

Também é possível executar sem argumento; nesse caso, o programa solicita o nome base ou caminho do arquivo no console:

```powershell
java -cp out Main
```

## Como gerar o JAR executável

Depois de compilar o projeto, gere o JAR executável com:

```powershell
jar cfe StaticChecker.jar Main -C out .
```

Se o comando `jar` não estiver no PATH, pode ser necessário usar o executável a partir do `JAVA_HOME`:

```powershell
& "$env:JAVA_HOME\bin\jar.exe" cfe StaticChecker.jar Main -C out .
```

Ou usar o caminho direto do JDK instalado, por exemplo:

```powershell
& "C:\Program Files\Java\jdk-25\bin\jar.exe" cfe StaticChecker.jar Main -C out .
```

## Como executar o JAR

Sem informar a extensão:

```powershell
java -jar StaticChecker.jar examples\TesteSintaticoValido
```

Informando a extensão:

```powershell
java -jar StaticChecker.jar examples\TesteSintaticoValido.261
```

## Arquivo de entrada

A entrada deve ser um arquivo texto com extensão `.261`.

O arquivo pode ser informado com ou sem extensão. Quando a extensão não é informada, o programa adiciona `.261` automaticamente.

## Saídas Geradas

Para a entrada:

```text
examples\TesteSintaticoValido.261
```

o programa gera:

```text
examples\TesteSintaticoValido.LEX
examples\TesteSintaticoValido.TAB
```

Os dois arquivos são criados na mesma pasta do arquivo fonte `.261`.

## Relatório .LEX

O arquivo `.LEX` contém os tokens reconhecidos na ordem em que aparecem no arquivo fonte.

Cada token apresenta o lexeme, o código do átomo, o índice na tabela de símbolos quando existir e a linha de ocorrência.

## Relatório .TAB

O arquivo `.TAB` contém o estado final da tabela de símbolos.

Cada entrada apresenta número da entrada, código, lexeme, quantidade de caracteres antes e depois da truncagem, tipo do símbolo e linhas de ocorrência.

## Análise Sintática

Após a análise léxica e a geração dos relatórios `.LEX` e `.TAB`, o programa executa a análise sintática.

Se o arquivo estiver sintaticamente correto, o programa exibe mensagem de sucesso.

Se houver erro, o programa informa um erro sintático controlado, com lexeme, código, submáquina, estado e item esperado, sem imprimir stack trace.

Arquivos usados apenas para teste léxico podem gerar `.LEX` e `.TAB` corretamente, mas depois acusar erro sintático por não representarem um programa completo.

## Erros comuns

- `jar` não reconhecido no terminal: configure o PATH do JDK, configure `JAVA_HOME` ou use o caminho completo de `jar.exe`.
- `JAVA_HOME` ausente ou incorreto: aponte `JAVA_HOME` para a pasta raiz do JDK, não para a pasta `bin`.
- Scripts locais no PowerShell: execute usando `.\nome-do-script.bat` ou `.\nome-do-script.ps1`.
- Arquivo inexistente ou caminho errado: confira se o arquivo `.261` existe e se o caminho relativo está correto a partir da pasta do projeto.
- Arquivo apenas léxico: exemplos como testes simples de tokens podem gerar `.LEX` e `.TAB`, mas ainda assim apresentar erro sintático no final.

## Arquivos recomendados para entrega final

```text
StaticChecker.jar
README.md
PROJ atualizado
src/
examples/*.261
documentos das etapas TBNF, TWIR, SIMP, BRUT e OTIM
```

A pasta `out/` é gerada durante a compilação e não precisa ser enviada na entrega.

## Execução rápida

Compilar:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac --release 17 -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName })
```

Gerar JAR:

```powershell
jar cfe StaticChecker.jar Main -C out .
```

Rodar:

```powershell
java -jar StaticChecker.jar examples\TesteSintaticoValido
```
