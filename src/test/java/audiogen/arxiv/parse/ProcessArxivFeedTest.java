package audiogen.arxiv.parse;

import audiogen.config.AppConfig;
import io.bitken.tts.model.entity.ArxivOai;
import io.bitken.tts.model.entity.PaperData;
import io.bitken.tts.repo.ArxivOaiRepo;
import io.bitken.tts.repo.PaperCategoryRepo;
import io.bitken.tts.repo.PaperDataRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@SpringBootTest(classes = {AppConfig.class})
public class ProcessArxivFeedTest {

    @Autowired
    ArxivOaiRepo oaiRepo;

    @Autowired
    PaperDataRepo paperDataRepo;

    @Autowired
    PaperCategoryRepo paperCategoryRepo;

    @BeforeEach
    public void beforeEach() throws IOException {
        ArxivOai oai = new ArxivOai();
        oai.setBatchId(1L);
        oai.setOaiXml(StreamUtils.copyToString(
            new ClassPathResource("oai-arxiv-raw-small.xml").getInputStream(), StandardCharsets.UTF_8)
        );
        ArxivOai.Source src = new ArxivOai.Source();
        src.setUrl("http://test.localhost");
        oai.setSrc(src);

        oaiRepo.save(oai);
    }

    @Test
    public void testPaperFieldNewlines() throws IOException {
        new ProcessArxivFeed(oaiRepo, paperDataRepo, paperCategoryRepo).processLatestBatch();
        List<PaperData> papers = paperDataRepo.findAll();
        for (PaperData paper : papers) {
            Assertions.assertFalse(paper.getTitle().contains("\n"));
            Assertions.assertFalse(paper.getTitle().contains("\r"));
            Assertions.assertFalse(paper.getAuthors().contains("\n"));
            Assertions.assertFalse(paper.getAuthors().contains("\r"));
        }
    }

}
