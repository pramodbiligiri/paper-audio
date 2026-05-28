package audiogen.usecase;

import audiogen.domain.PaperToTtsInput;
import audiogen.usecase.port.ArxivSource;
import audiogen.usecase.port.OaiArchive;
import audiogen.usecase.port.PaperGateway;
import audiogen.usecase.port.RecordParser;
import audiogen.usecase.port.TtsEngine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Verifies that use-case constructors accept only port interfaces — no framework
 * or external-library types. Fails to compile until all skeletons exist.
 */
public class UseCaseConstructorTest {

    @Test
    void generateAudioForCategory_acceptsOnlyPorts() {
        PaperGateway papers = mock(PaperGateway.class);
        TtsEngine tts = mock(TtsEngine.class);
        PaperToTtsInput ttsInput = new PaperToTtsInput();

        GenerateAudioForCategory useCase = new GenerateAudioForCategory(papers, tts, ttsInput);

        assertNotNull(useCase);
    }

    @Test
    void ingestArxivFeed_acceptsOnlyPorts() {
        OaiArchive archive = mock(OaiArchive.class);
        PaperGateway papers = mock(PaperGateway.class);
        RecordParser parser = mock(RecordParser.class);

        IngestArxivFeed useCase = new IngestArxivFeed(archive, papers, parser);

        assertNotNull(useCase);
    }

    @Test
    void fetchArxivFeed_acceptsOnlyPorts() {
        ArxivSource source = mock(ArxivSource.class);
        OaiArchive archive = mock(OaiArchive.class);

        FetchArxivFeed useCase = new FetchArxivFeed(source, archive);

        assertNotNull(useCase);
    }

    // Inline stub factory — keeps this test free of Mockito on the classpath check.
    // If Mockito is already a test dep, replace these with Mockito.mock().
    @SuppressWarnings("unchecked")
    private <T> T mock(Class<T> type) {
        return (T) java.lang.reflect.Proxy.newProxyInstance(
            type.getClassLoader(),
            new Class[]{type},
            (proxy, method, args) -> null
        );
    }
}
