package audiogen.usecase.model;

import java.util.Map;

public final class OaiPage {

    private final long batchId;
    private final String oaiXml;
    private final String sourceUrl;
    private final Map<String, String> sourceParams;

    public OaiPage(long batchId, String oaiXml, String sourceUrl, Map<String, String> sourceParams) {
        this.batchId = batchId;
        this.oaiXml = oaiXml;
        this.sourceUrl = sourceUrl;
        this.sourceParams = sourceParams;
    }

    public long getBatchId() { return batchId; }
    public String getOaiXml() { return oaiXml; }
    public String getSourceUrl() { return sourceUrl; }
    public Map<String, String> getSourceParams() { return sourceParams; }
}
