package audiogen.arxiv.records;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Metadata {

    @JacksonXmlProperty(localName = "arXivRaw", namespace = "http://arxiv.org/OAI/arXivRaw/")
    private ArxivRaw arxivRaw;

    public ArxivRaw getArxivRaw() {
        return arxivRaw;
    }
}
