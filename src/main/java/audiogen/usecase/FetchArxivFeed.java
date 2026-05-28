package audiogen.usecase;

import audiogen.usecase.port.ArxivSource;
import audiogen.usecase.port.OaiArchive;

import java.io.IOException;

public class FetchArxivFeed {

    private final ArxivSource source;
    private final OaiArchive archive;

    public FetchArxivFeed(ArxivSource source, OaiArchive archive) {
        this.source = source;
        this.archive = archive;
    }

    public void fetch() throws IOException {
        throw new UnsupportedOperationException("Implemented in Task 4");
    }
}
