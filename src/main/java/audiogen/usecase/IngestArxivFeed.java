package audiogen.usecase;

import audiogen.usecase.model.IngestResult;
import audiogen.usecase.port.OaiArchive;
import audiogen.usecase.port.PaperGateway;
import audiogen.usecase.port.RecordParser;

import java.io.IOException;

public class IngestArxivFeed {

    private final OaiArchive archive;
    private final PaperGateway papers;
    private final RecordParser parser;

    public IngestArxivFeed(OaiArchive archive, PaperGateway papers, RecordParser parser) {
        this.archive = archive;
        this.papers = papers;
        this.parser = parser;
    }

    public IngestResult ingestLatestBatch() throws IOException {
        throw new UnsupportedOperationException("Implemented in Task 4");
    }
}
