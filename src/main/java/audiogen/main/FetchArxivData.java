package audiogen.main;

import audiogen.arxiv.fetch.ArxivOaiFetcher;
import audiogen.tts.Synchronizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.text.ParseException;

@Component
@ComponentScan (
    basePackages = {"audiogen"},
    excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "audiogen.main.*")
)
public class FetchArxivData implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(FetchArxivData.class);

    @Autowired
    private ArxivOaiFetcher arxivOaiFetcher;

    @Autowired
    private Synchronizer syncer;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(FetchArxivData.class);
        app.run(args);
    }

    @Override
    public void run(String[] args) throws IOException, ParseException {
        LOG.info("Running FetchArxivData ...");
        arxivOaiFetcher.fetch();
        LOG.info("Finished running FetchArxivData");
    }

}