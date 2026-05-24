package util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// Responsavel por localizar e ler o arquivo-fonte .261.
public class SourceReader {

    public Path locateSourceFile(String inputNameOrPath) throws IOException {
        if (inputNameOrPath == null || inputNameOrPath.isBlank()) {
            throw new IllegalArgumentException("Nome do arquivo nao pode ser vazio.");
        }

        String normalizedInput = ensure261Extension(inputNameOrPath.trim());
        Path candidate = Paths.get(normalizedInput);

        if (!candidate.isAbsolute()) {
            candidate = Path.of("").toAbsolutePath().resolve(candidate).normalize();
        } else {
            candidate = candidate.normalize();
        }

        if (!Files.exists(candidate) || !Files.isRegularFile(candidate)) {
            throw new IOException("Arquivo .261 nao encontrado: " + candidate);
        }

        return candidate;
    }

    public String readAllContent(Path sourcePath) throws IOException {
        return Files.readString(sourcePath, StandardCharsets.UTF_8);
    }

    private String ensure261Extension(String rawInput) {
        if (rawInput.toLowerCase().endsWith(".261")) {
            return rawInput;
        }
        return rawInput + ".261";
    }
}
