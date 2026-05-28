package audiogen.usecase.port;

import audiogen.usecase.model.FetchRequest;

import java.io.IOException;
import java.util.Optional;

public interface ArxivSource {
    String fetchPage(FetchRequest request) throws IOException;
    Optional<String> resumptionToken(String oaiXml) throws IOException;
}
