package audiogen.arxiv.parse;

import audiogen.arxiv.records.Record;
import io.bitken.tts.repo.ArxivOaiRepo;
import java.io.IOException;
import java.util.Iterator;

public class ArxivRecords implements Iterable<Record> {

    private ArxivOaiRepo oaiRepo;
    private long startingBatchId;

    public ArxivRecords(ArxivOaiRepo oaiRepo, long startingBatchId) {
        this.oaiRepo = oaiRepo;
        this.startingBatchId = startingBatchId;
    }

    @Override
    public Iterator<Record> iterator() {
        try {
            return new ArxivRecordsIterator(oaiRepo, startingBatchId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
