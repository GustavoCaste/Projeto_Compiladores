# Static Checker — Klebão2026-1

Projeto acadêmico da disciplina **Compiladores** (UCSal / Engenharia de Software).  
Implementa a **Etapa 7 (LEX)**: análise léxica completa com geração de tabela de símbolos e relatórios.

---

## O que o programa faz — visão geral

```
Arquivo .261  →  [Analisador Léxico]  →  Tokens  →  [Relatórios]  →  .LEX  +  .TAB
```

| Entrada | Processamento | Saída |
|---------|--------------|-------|
| Arquivo texto com código-fonte `.261` | Leitura caractere a caractere, reconhecimento de átomos, tabela de símbolos | Relatório `.LEX` (todos os tokens na ordem) + `.TAB` (tabela de símbolos final) |

---

## Tecnologias

- Java 17 (OpenJDK 17 LTS)  
- `javac` + `java` via terminal (sem Maven, Gradle ou bibliotecas externas)

---

## Estrutura de pastas

```text
Projeto_Compiladores/
├── src/
│   ├── Main.java                          ← ponto de entrada
│   ├── lexer/
│   │   ├── LexicalAnalyzer.java           ← analisador léxico (char a char)
│   │   ├── Token.java                     ← representa um átomo encontrado
│   │   └── TokenCode.java                 ← constantes dos códigos (A01..C07)
│   ├── reserved/
│   │   └── ReservedTable.java             ← palavras e símbolos reservados (fixa)
│   ├── symboltable/
│   │   ├── SymbolTable.java               ← tabela de símbolos (só identificadores)
│   │   └── SymbolEntry.java               ← uma entrada da tabela
│   ├── report/
│   │   ├── LexReportWriter.java           ← gera o arquivo .LEX
│   │   ├── SymbolTableReportWriter.java   ← gera o arquivo .TAB
│   │   └── ReportHeader.java              ← cabeçalho padrão dos dois relatórios
│   ├── util/
│   │   └── SourceReader.java             ← localiza e lê o arquivo .261
│   └── parser/
│       └── Parser.java                    ← analisador sintático (bônus)
└── examples/
    ├── MeuTeste.261
    ├── TesteBasico.261
    ├── TesteTruncagem.261
    └── ...
```

---

## Como compilar

No terminal PowerShell, dentro da pasta `Projeto_Compiladores/`:

```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName })
```

Sem erros → pasta `out/` é populada com os `.class`.

---

## Como executar — passo a passo

### Passo 1 — Forneça o nome do arquivo (sem a extensão `.261`)

```powershell
java -cp out Main examples/MeuTeste
```

O programa automaticamente procura `examples/MeuTeste.261`.

- Se o nome não tiver caminho → procura no **diretório corrente**.  
- Se tiver caminho (`C:\fontes\MeuTeste`) → usa o caminho informado.  
- Também aceita o nome **com** a extensão (`.261`), que é removida e recolocada normalmente.

Sem argumento, o programa pede no console:

```powershell
java -cp out Main
Informe o nome base ou caminho do arquivo .261: examples/MeuTeste
```

---

### Passo 2 — O analisador léxico lê o fonte caractere a caractere

Dado o arquivo de entrada `examples/MeuTeste.261`:

```
var1 var2 var3
var1 var1 var1
var2 var2 var1
; := [ real
```

O léxico percorre cada caractere aplicando as seguintes regras:

| Situação | O que acontece |
|----------|---------------|
| Espaços e quebras de linha | Descartados (apenas delimitam átomos) |
| `//` | Descarta tudo até o fim da linha (comentário de linha) |
| `/* … */` | Descarta tudo até `*/`; se não fechar, descarta até o fim do arquivo |
| Letra ou `_` | Inicia leitura de identificador ou palavra reservada |
| Dígito | Inicia leitura de constante inteira ou real |
| `"` | Inicia leitura de constante string |
| `'` | Inicia leitura de constante char |
| `:=` `==` `!=` `<=` `>=` | Símbolos reservados de dois caracteres |
| `;` `,` `:` `+` `-` etc. | Símbolos reservados de um caractere |
| Caractere inválido | Descartado silenciosamente (o átomo continua sendo montado) |

**Regra de caixa única:** todo lexeme é convertido para **MAIÚSCULO** antes de ser reconhecido e armazenado. `Var1`, `VAR1` e `var1` são o **mesmo** símbolo.

**Limite de 30 caracteres:** só os 30 primeiros caracteres válidos formam o lexeme. O restante é descartado, mas a leitura continua até o delimitador.

---

### Passo 3 — Cada átomo é classificado e registrado

Para cada átomo formado:

```
"var1"  →  maiúsculo: "VAR1"
         →  não está na tabela de reservadas
         →  contexto atual: nenhuma declaração ativa → código C01 (variable)
         →  tabela de símbolos: novo símbolo, entrada 1
         →  token: { lexeme="VAR1", código="C01", índice=1, linha=1 }

"real"  →  maiúsculo: "REAL"
         →  encontrado na tabela de reservadas → código A20
         →  token: { lexeme="REAL", código="A20", índice=-1, linha=4 }

":="    →  símbolo de dois chars → código B04
         →  token: { lexeme=":=", código="B04", índice=-1, linha=4 }
```

Códigos dos átomos:

| Faixa | Tipo | Exemplos |
|-------|------|---------|
| A01–A26 | Palavras reservadas | `program`(A19), `integer`(A16), `if`(A15), `while`(A26) … |
| B01–B22 | Símbolos reservados | `;`(B01), `:=`(B04), `==`(B17), `!=`(B18), `#`(B18) … |
| C01–C07 | Identificadores | variable(C01), functionName(C02), programName(C03), stringConst(C04), charConst(C05), intConst(C06), realConst(C07) |

---

### Passo 4 — Controle de escopo atribui tipo ao símbolo

O léxico rastreia o contexto de declaração para saber o tipo de cada variável:

```
varType integer : contador , salario ;
  │       │    │     └──────────────── variáveis → tipo IN (integer escalar)
  │       │    └─────────────────────── delimitador de lista
  │       └──────────────────────────── tipo da declaração
  └──────────────────────────────────── marca início da declaração
```

Tipos registrados na tabela de símbolos:

| Sigla | Significado |
|-------|------------|
| `FP` | real escalar |
| `IN` | integer escalar |
| `ST` | string escalar |
| `CH` | character escalar |
| `BL` | boolean escalar |
| `VD` | void |
| `AF` | array de real |
| `AI` | array de integer |
| `AS` | array de string |
| `AC` | array de character |
| `AB` | array de boolean |
| `-`  | tipo desconhecido / não aplicável |

---

### Passo 5 — Relatórios são gerados na mesma pasta do `.261`

---

## Saída — Arquivo `.LEX`

Gerado em `examples/MeuTeste.LEX`. Contém **um átomo por linha**, na **ordem de aparição** no fonte (incluindo repetições).

**Exemplo real gerado para `MeuTeste.261`:**

```
Código da Equipe: EQ07
Projeto: Static Checker da Linguagem Klebão2026-1
Relatório: RELATÓRIO DA ANÁLISE LÉXICA
Arquivo Fonte: MeuTeste.261

Integrantes:
FILIPE MIRANDA DE OLIVEIRA — (71) 99103-1020 — e-mail: filipemiranda.oliveira@ucsal.edu.br
GUSTAVO CASTELLUCIO DA COSTA LIMA — (71) 99681-5124 — e-mail: gustavo.lima@ucsal.edu.br
LEONARDO BRITTO DA SILVA — (71) 99969-0054 — e-mail: leonardob.silva@ucsal.edu.br
LUCCA BARBOSA NYGAARD — (71) 99680-1901 — e-mail: lucca.nygaard@ucsal.edu.br

Conteudo do texto fonte analisado:
var1 var2 var3
var1 var1 var1
var2 var2 var1
; := [ real

------------------------------------------------------------
Tokens:
Lexeme: VAR1, Codigo: C01, indiceTabSimb: 1, Linha: 1.
Lexeme: VAR2, Codigo: C01, indiceTabSimb: 2, Linha: 1.
Lexeme: VAR3, Codigo: C01, indiceTabSimb: 3, Linha: 1.
Lexeme: VAR1, Codigo: C01, indiceTabSimb: 1, Linha: 2.
Lexeme: VAR1, Codigo: C01, indiceTabSimb: 1, Linha: 2.
Lexeme: VAR1, Codigo: C01, indiceTabSimb: 1, Linha: 2.
Lexeme: VAR2, Codigo: C01, indiceTabSimb: 2, Linha: 3.
Lexeme: VAR2, Codigo: C01, indiceTabSimb: 2, Linha: 3.
Lexeme: VAR1, Codigo: C01, indiceTabSimb: 1, Linha: 3.
Lexeme: ;,    Codigo: B01, indiceTabSimb: -, Linha: 4.
Lexeme: :=,   Codigo: B04, indiceTabSimb: -, Linha: 4.
Lexeme: [,    Codigo: B08, indiceTabSimb: -, Linha: 4.
Lexeme: REAL, Codigo: A20, indiceTabSimb: -, Linha: 4.
```

**O que cada campo significa:**

| Campo | Descrição |
|-------|-----------|
| `Lexeme` | O texto do átomo, em maiúsculo, limitado a 30 chars |
| `Codigo` | Código do átomo (A=reservada, B=símbolo, C=identificador) |
| `indiceTabSimb` | Posição na tabela de símbolos; `-` para palavras/símbolos reservados |
| `Linha` | Número da linha onde o átomo começa no fonte |

---

## Saída — Arquivo `.TAB`

Gerado em `examples/MeuTeste.TAB`. Contém o **estado final** da tabela de símbolos — apenas identificadores (C01–C07), sem repetição, um por linha.

**Exemplo real gerado para `MeuTeste.261`:**

```
Código da Equipe: EQ07
Projeto: Static Checker da Linguagem Klebão2026-1
Relatório: RELATÓRIO DA TABELA DE SÍMBOLOS
Arquivo Fonte: MeuTeste.261

Integrantes:
...

------------------------------------------------------------
Entradas:
Entrada: 1, Codigo: C01, Lexeme: VAR1, QtdCharsAntesTrunc: 4, QtdCharDepoisTrunc: 4, TipoSimb: -, Linhas: (1, 2, 2, 2, 3).
Entrada: 2, Codigo: C01, Lexeme: VAR2, QtdCharsAntesTrunc: 4, QtdCharDepoisTrunc: 4, TipoSimb: -, Linhas: (1, 3, 3).
Entrada: 3, Codigo: C01, Lexeme: VAR3, QtdCharsAntesTrunc: 4, QtdCharDepoisTrunc: 4, TipoSimb: -, Linhas: (1).
```

**O que cada campo significa:**

| Campo | Descrição |
|-------|-----------|
| `Entrada` | Índice fixo do símbolo (ordem de aparição, começa em 1) |
| `Codigo` | Código do tipo de identificador (C01..C07) |
| `Lexeme` | Texto em maiúsculo, truncado em 30 chars |
| `QtdCharsAntesTrunc` | Total de caracteres válidos lidos (sem contar inválidos descartados; aspas contam) |
| `QtdCharDepoisTrunc` | Tamanho do lexeme após truncagem (máx. 30) |
| `TipoSimb` | Sigla do tipo (`IN`, `FP`, `ST` …) ou `-` se não declarado |
| `Linhas` | As até 5 primeiras linhas onde o símbolo aparece (com repetição) |

---

## Exemplo completo — truncagem e tipo

**Entrada** `examples/TesteTruncagem.261`:

```
program MisturaCase

declarations
varType integer: IdentificadorComMaisDeTrintaCaracteresValidosABC123;
varType string: textoLongo;
endDeclararions

identificadorcommAisdetRintacaracteresvalidosabc123 := 12345678901234567890123456789012345;
textoLongo := "ABCDEFGHIJ ABCDEFGHIJ ABCDEFGHIJ ABCDEFGHIJ";

endProgram
```

**Saída `.TAB` (entradas relevantes):**

```
Entrada: 2, Codigo: C01, Lexeme: IDENTIFICADORCOMMAISDETRINTACA, QtdCharsAntesTrunc: 51, QtdCharDepoisTrunc: 30, TipoSimb: IN, Linhas: (4, 8).
Entrada: 3, Codigo: C01, Lexeme: TEXTOLONGO,                    QtdCharsAntesTrunc: 10, QtdCharDepoisTrunc: 10, TipoSimb: ST, Linhas: (5, 9).
Entrada: 4, Codigo: C06, Lexeme: 123456789012345678901234567890, QtdCharsAntesTrunc: 35, QtdCharDepoisTrunc: 30, TipoSimb: -, Linhas: (8).
Entrada: 5, Codigo: C04, Lexeme: "ABCDEFGHIJ ABCDEFGHIJ ABCDEFG, QtdCharsAntesTrunc: 45, QtdCharDepoisTrunc: 30, TipoSimb: -, Linhas: (9).
```

O que aconteceu:
- Identificador de 51 chars → lexeme truncado para os 30 primeiros → `AntesTrunc: 51`, `DepoisTrunc: 30`  
- `IDENTIFICADORCOMMAISDETRINTACA` na linha 4 (declaração `varType integer`) → tipo `IN`  
- A mesma variável (case misto) na linha 8 é o **mesmo símbolo** (caixa única)  
- intConst de 35 dígitos → truncado para 30  
- String de 45 chars (incluindo aspas) → truncada para 30

---

## Mensagens de console

Durante a execução o programa imprime:

```
Arquivo localizado: C:\...\examples\MeuTeste.261
Leitura do arquivo concluida.
Analise lexica executada. Tokens: 13
Relatorios gerados com sucesso:
 - C:\...\examples\MeuTeste.LEX
 - C:\...\examples\MeuTeste.TAB
Analise sintatica executada com sucesso.   ← ou erro sintático, se houver
```

Os relatórios `.LEX` e `.TAB` são sempre gerados mesmo quando há erro sintático.

---

## Observações

- Arquivos gerados em **UTF-8**; acentos corretos ao abrir no editor (VS Code, Notepad++).
- O projeto não usa Maven, Gradle ou bibliotecas externas.
- A pasta `out/` deve existir ou ser criada antes de compilar (`mkdir out`).
