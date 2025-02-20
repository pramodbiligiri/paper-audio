package audiogen.arxiv.records;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.List;

public class ListRecords {

    @JacksonXmlProperty(localName = "record")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Record> records;

    @JacksonXmlProperty(localName = "resumptionToken")
    String resumptionToken;

    public List<Record> getRecords() {
        return records;
    }

    // TODO: Convert to Optional<String>
    public String getResumptionToken() {
        return resumptionToken;
    }
}
