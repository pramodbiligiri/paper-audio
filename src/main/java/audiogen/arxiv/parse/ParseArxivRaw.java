package audiogen.arxiv.parse;

import audiogen.arxiv.records.OaiFeed;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import java.io.IOException;

public class ParseArxivRaw {

    private static final Logger LOG = LoggerFactory.getLogger(ParseArxivRaw.class);

    private final XmlMapper mapper;

    public ParseArxivRaw() {
        mapper = XmlMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();
    }

    public OaiFeed parse(Resource resource) throws IOException {
        return mapper.readValue(resource.getInputStream(), OaiFeed.class);
    }

    public OaiFeed parse(String oaiXml) throws IOException {
        return mapper.readValue(oaiXml, OaiFeed.class);
    }

}
