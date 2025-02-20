package audiogen.arxiv.parse;

import audiogen.arxiv.records.ArxivRaw;
import audiogen.arxiv.records.OaiFeed;
import audiogen.arxiv.records.Record;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;
import java.util.List;

public class ParseArxivRawTest {

    @Test
    public void testFieldsHaveNoNewlines() throws IOException {
        ParseArxivRaw parser = new ParseArxivRaw();

        OaiFeed feed = parser.parse(new ClassPathResource("oai-1.xml"));

        List<Record> records = feed.getListRecords().getRecords();
        for (Record rec : records) {
            ArxivRaw arxivRaw = rec.getMetadata().getArxivRaw();
            Assertions.assertFalse(arxivRaw.getTitle().contains("\n"));
            Assertions.assertFalse(arxivRaw.getTitle().contains("\r"));

            Assertions.assertFalse(arxivRaw.getAuthors().contains("\n"));
            Assertions.assertFalse(arxivRaw.getAuthors().contains("\r"));
        }

    }
}
