package audiogen.main;

import audiogen.arxiv.parse.ArxivRecords;
import audiogen.arxiv.records.ArxivRaw;
import audiogen.arxiv.records.Metadata;
import audiogen.arxiv.records.Record;
import io.bitken.tts.repo.ArxivOaiRepo;
import io.bitken.tts.repo.PaperAudioRepo;
import io.bitken.tts.repo.PaperDataRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
@ComponentScan(
    basePackages = {"audiogen"},
    excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "audiogen.main.*")
)
public class ReportArxivPaper implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(ReportArxivPaper.class);

    @Autowired
    private ArxivOaiRepo oaiRepo;

    @Autowired
    private PaperDataRepo paperDataRepo;

    @Autowired
    private PaperAudioRepo paperAudioRepo;

    public static void main(String[] args) throws IOException {
        SpringApplication app = new SpringApplication(ReportArxivPaper.class);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        ArxivRecords records = new ArxivRecords(oaiRepo, oaiRepo.findSmallestBatchId());

        long i = 0;
        for (Record record : records) {
            Metadata metadata = record.getMetadata();
            ArxivRaw arxivRaw = metadata.getArxivRaw();
            String arxivId = arxivRaw.getArxivId();
            boolean paperDataExists = false, audioExists = false;

            paperDataExists = paperDataRepo.checkExists(arxivId);
            if (paperDataExists) {
                audioExists = paperAudioRepo.checkExists(paperDataRepo.findByArxivId(arxivId).get(0).getId());
            }

            System.out.println(arxivId + "\t" + paperDataExists + "\t" + audioExists + "\t" + arxivRaw.getCategories());

            i++;
        }

        LOG.info("Examined " + i + " records");

    }
}
