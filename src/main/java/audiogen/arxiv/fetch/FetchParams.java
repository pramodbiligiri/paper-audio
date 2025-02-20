package audiogen.arxiv.fetch;

import audiogen.arxiv.parse.ParseArxivRaw;
import audiogen.arxiv.records.OaiFeed;
import io.bitken.tts.repo.ArxivOaiRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class FetchParams {

    private static final Logger LOG = LoggerFactory.getLogger(FetchParams.class);

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat responseDateFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private final String from;
    private final String set;
    private final String verb;
    private final String metadataPrefix;

    private Optional<String> resumptionToken;

    private Optional<Long> batchId;

    public FetchParams(ArxivOaiRepo repo, ParseArxivRaw parser) throws IOException, ParseException {
        this.verb = "ListRecords";
        this.set = "cs";
        this.metadataPrefix = "arXivRaw";
        this.resumptionToken = Optional.empty();

        String xml = repo.findMostRecentXml();
        if (xml == null || xml.isEmpty()) {
            from = sdf.format(new Date().getTime());
            LOG.info("Setting from based on current timestamp: " + from);
        } else {
            OaiFeed feed = parser.parse(xml);
            Date prevDate = responseDateFmt.parse(feed.getResponseDate());
            LOG.info("Previous response date: " + prevDate);
            from = sdf.format(prevDate);
            LOG.info("Setting from based on prev date: " + from);
        }
    }

    public Map<String, String> asMap() {
        Map<String, String> map = new HashMap<>();
        map.put("verb", verb);
        if (resumptionToken.isPresent()) {
            map.put("resumptionToken", resumptionToken.get());
        } else {
            map.put("set", set);
            map.put("metadataPrefix", metadataPrefix);
            map.put("from", from);
        }

        return map;
    }

    public void setBatchId(Optional<Long> batchId) {
        this.batchId = batchId;
    }

    public Optional<Long> getBatchId() {
        return batchId;
    }

    public String getVerb() {
        return verb;
    }

    public void setResumptionToken(String resumptionToken) {
        this.resumptionToken = Optional.of(resumptionToken);
    }
}
