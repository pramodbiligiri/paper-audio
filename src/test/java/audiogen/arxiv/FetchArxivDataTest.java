package audiogen.arxiv;

import audiogen.config.AppConfig;
import audiogen.main.FetchArxivData;
import audiogen.arxiv.fetch.ArxivOaiFetcher;
import io.bitken.tts.model.entity.ArxivOai;
import io.bitken.tts.repo.ArxivOaiRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

@SpringBootTest(classes = {AppConfig.class, FetchArxivData.class})
public class FetchArxivDataTest {

    private static final Logger LOG = LoggerFactory.getLogger(FetchArxivDataTest.class);

    @MockBean
    ArxivOaiFetcher arxivFetcher;

    @Autowired
    FetchArxivData fetchArxivData;

    @Autowired
    ArxivOaiRepo arxivOaiRepo;

    @BeforeEach
    public void beforeEach() throws IOException, ParseException {
        final String xml = FileCopyUtils.copyToString(
            new InputStreamReader(
                new ClassPathResource("oai-arxiv-raw-small.xml").getInputStream(),
                StandardCharsets.UTF_8
            )
        );

        Mockito.doAnswer((Answer<Void>) invocation -> {
            ArxivOai arxivOai = new ArxivOai();
            arxivOai.setOaiXml(xml);
            arxivOai.setBatchId(1L);

            ArxivOai.Source src = new ArxivOai.Source();
            src.setUrl("http://local.mock");
            src.setParams(new HashMap<>());
            arxivOai.setSrc(src);
            arxivOaiRepo.save(arxivOai);

            return null;
        }).when(arxivFetcher).fetch();
    }

    @Test
    public void testFetchArxivData() throws IOException, ParseException {
        fetchArxivData.run(new String[]{});
        List<ArxivOai> oais = arxivOaiRepo.findAll();
        Assertions.assertEquals(1, oais.size());
        ArxivOai oai = oais.get(0);
        Assertions.assertEquals(1L, oai.getBatchId());
    }

}
