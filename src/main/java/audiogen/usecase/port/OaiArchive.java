package audiogen.usecase.port;

import audiogen.usecase.model.OaiPage;

import java.util.List;

public interface OaiArchive {
    long save(OaiPage page);
    long maxBatchId();
    List<OaiPage> byBatchId(long batchId);
    String mostRecentXml();
}
