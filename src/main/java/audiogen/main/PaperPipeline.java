package audiogen.main;

import audiogen.arxiv.fetch.ArxivOaiFetcher;
import audiogen.arxiv.parse.ParseResult;
import audiogen.arxiv.parse.ProcessArxivFeed;
import audiogen.tts.Synchronizer;
import io.bitken.tts.model.domain.CategoryInfo;
import io.bitken.tts.repo.ArxivOaiRepo;
import io.bitken.tts.repo.PaperCategoryRepo;
import io.bitken.tts.repo.PaperDataRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;

@Component
@ComponentScan(
   basePackages = {"audiogen"},
   excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "audiogen.main.*")
)
public class PaperPipeline implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(PaperPipeline.class);

    @Autowired
    private ArxivOaiFetcher arxivOaiFetcher;

    @Autowired
    private ArxivOaiRepo oaiRepo;

    @Autowired
    private PaperDataRepo paperDataRepo;

    @Autowired
    private PaperCategoryRepo paperCategoryRepo;

    @Autowired
    private Synchronizer syncer;

    @Value("${data.refresh.papers.count:1}")
    private int dataRefreshPapersCount;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(PaperPipeline.class);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info("Starting paper pipeline ...");

        LOG.info("Fetching Arxiv data ...");
        arxivOaiFetcher.fetch();
        LOG.info("Finished fetching Arxiv data");

        LOG.info("Parsing fetched data ...");
        ProcessArxivFeed processArxivFeed = new ProcessArxivFeed(oaiRepo, paperDataRepo, paperCategoryRepo);
        ParseResult parseResult = processArxivFeed.processLatestBatch();
        LOG.info("Finished parsing fetched data");

        if (parseResult.hasNoNewPapers()) {
            LOG.info("No new data after parsing. Skipping audio generation. " +
                    "Parse Results: " + parseResult);
        } else {
            generateAudio();
        }

        syncer.close();

        LOG.info("Finished paper pipeline");
    }

    private void generateAudio() {
        LOG.info("Generating audio for new data ...");

        for (CategoryInfo cat : CategoryInfo.values()) {
            for (String arxivCat : cat.getArxivCats()) {
                try {
                    syncer.generateAudio(dataRefreshPapersCount, arxivCat);
                } catch (Exception e) {
                    // Catch top-level exception so that error in one category doesn't mean
                    // you stop the whole run
                    LOG.error("Error generating audio for category: " + arxivCat, e);
                }
            }
        }

        LOG.info("Finished generating audio for new data");
    }
}
