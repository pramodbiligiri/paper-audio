package audiogen.usecase;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Guards the usecase layer against forbidden dependencies.
 * Parses import statements directly — no ArchUnit needed until Task 6.
 */
public class UseCaseImportBoundaryTest {

    private static final String USECASE_SRC =
        "src/main/java/audiogen/usecase";

    private static final List<String> FORBIDDEN_PREFIXES = List.of(
        "import io.bitken.",
        "import com.google.",
        "import org.springframework."
    );

    @Test
    void usecaseLayer_hasNoForbiddenImports() throws IOException {
        Path root = Paths.get(USECASE_SRC);
        if (!root.toFile().exists()) {
            fail("usecase source directory not found: " + root.toAbsolutePath());
        }

        try (Stream<Path> files = Files.walk(root)) {
            List<String> violations = files
                .filter(p -> p.toString().endsWith(".java"))
                .flatMap(p -> linesOf(p).stream()
                    .filter(line -> FORBIDDEN_PREFIXES.stream().anyMatch(line::startsWith))
                    .map(line -> p.getFileName() + ": " + line.trim()))
                .collect(Collectors.toList());

            if (!violations.isEmpty()) {
                fail("Forbidden imports found in audiogen.usecase:\n  " +
                    String.join("\n  ", violations));
            }
        }
    }

    private List<String> linesOf(Path p) {
        try {
            return Files.readAllLines(p);
        } catch (IOException e) {
            throw new RuntimeException("Could not read " + p, e);
        }
    }
}
