package audiogen.arxiv.parse;

import audiogen.arxiv.records.ListRecords;
import audiogen.arxiv.records.OaiFeed;
import audiogen.arxiv.records.Record;
import io.bitken.tts.model.entity.ArxivOai;
import io.bitken.tts.repo.ArxivOaiRepo;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class ArxivRecordsIterator implements Iterator<Record> {

    private final ArxivOaiRepo oaiRepo;
    private final long startingBatchId;
    private final ParseArxivRaw parser;

    private Iterator<Long> batchIdIter;
    private Iterator<ArxivOai> oaiIter;
    private Iterator<Record> recordsIter;

    Record next = null;

    public ArxivRecordsIterator(ArxivOaiRepo oaiRepo, long startingBatchId) throws IOException {
        this.oaiRepo = oaiRepo;
        this.startingBatchId = startingBatchId;
        parser = new ParseArxivRaw();

        batchIdIter = oaiRepo.findBatchIdsGreaterOrEqualTo(startingBatchId).iterator();

        if (batchIdIter.hasNext()) {
            oaiIter = oaiRepo.findByBatchId(startingBatchId).iterator();
            if (oaiIter.hasNext()) {
                ArxivOai oai = oaiIter.next();
                OaiFeed feed = parser.parse(oai.getOaiXml());
                ListRecords listRecords = feed.getListRecords();
                List<Record> records = listRecords.getRecords();
                recordsIter = records.iterator();
            }
        }
    }

    @Override
    public boolean hasNext() {
        if (next != null) {
            return true;
        }

        if (recordsIter.hasNext()) {
            next = recordsIter.next();
            return true;
        }

        if (oaiIter.hasNext()) {
            ArxivOai oai = oaiIter.next();
            try {
                OaiFeed feed = parser.parse(oai.getOaiXml());
                ListRecords listRecords = feed.getListRecords();
                List<Record> records = listRecords.getRecords();
                recordsIter = records.iterator();
                return hasNext();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (batchIdIter.hasNext()) {
            long nextBatchId = batchIdIter.next();
            oaiIter = oaiRepo.findByBatchId(nextBatchId).iterator();
            return hasNext();
        }

        return false;
    }

    @Override
    public Record next() {
        if (hasNext()) {
            Record temp = next;
            next = null;
            return temp;
        }

        return null;
    }
}
