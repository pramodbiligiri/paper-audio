package audiogen.main;

import audiogen.arxiv.parse.ProcessArxivFeed;
import io.bitken.tts.repo.ArxivOaiRepo;
import io.bitken.tts.repo.PaperCategoryRepo;
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
public class GeneratePaperInfo implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(GeneratePaperInfo.class);

    @Autowired
    private ArxivOaiRepo oaiRepo;

    @Autowired
    private PaperDataRepo paperDataRepo;

    @Autowired
    private PaperCategoryRepo paperCategoryRepo;

    public static void main(String[] args) throws IOException {
        SpringApplication app = new SpringApplication(GeneratePaperInfo.class);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        new ProcessArxivFeed(oaiRepo, paperDataRepo, paperCategoryRepo).processLatestBatch();
    }
}
