package audiogen.arxiv.records;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Record {

    @JacksonXmlProperty(localName = "header")
    private Header header;

    @JacksonXmlProperty(localName = "metadata")
    private Metadata metadata;

    public Header getHeader() {
        return header;
    }

    public Metadata getMetadata() {
        return metadata;
    }
}
