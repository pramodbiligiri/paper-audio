package audiogen.arxiv.records;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Header {

    @JacksonXmlProperty(localName = "identifier")
    private String identifier;

    @JacksonXmlProperty(localName = "datestamp")
    private String datestamp;

    @JacksonXmlProperty(localName = "setSpec")
    private String spec;

    public String getIdentifier() {
        return identifier;
    }

    public String getDatestamp() {
        return datestamp;
    }

    public String getSpec() {
        return spec;
    }
}
