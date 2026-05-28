package audiogen.usecase.port;

import audiogen.usecase.model.ArxivRecord;

import java.util.List;

public interface RecordParser {
    List<ArxivRecord> parse(String oaiXml);
}
