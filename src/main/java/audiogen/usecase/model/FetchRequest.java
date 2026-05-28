package audiogen.usecase.model;

import java.util.Optional;

public final class FetchRequest {

    private final String from;
    private final String set;
    private final String metadataPrefix;
    private final Optional<String> resumptionToken;

    public FetchRequest(String from, String set, String metadataPrefix,
                        Optional<String> resumptionToken) {
        this.from = from;
        this.set = set;
        this.metadataPrefix = metadataPrefix;
        this.resumptionToken = resumptionToken;
    }

    public String getFrom() { return from; }
    public String getSet() { return set; }
    public String getMetadataPrefix() { return metadataPrefix; }
    public Optional<String> getResumptionToken() { return resumptionToken; }
}
