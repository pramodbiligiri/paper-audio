package audiogen.arxiv.records;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class OaiFeed {

    @JacksonXmlProperty(localName = "responseDate")
    String responseDate;

    @JacksonXmlProperty(localName = "ListRecords")
    ListRecords listRecords;

    public String getResponseDate() {
        return responseDate;
    }

    public ListRecords getListRecords() {
        return listRecords;
    }
}
