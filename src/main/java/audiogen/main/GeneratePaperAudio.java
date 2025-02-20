package audiogen.main;

import audiogen.tts.Synchronizer;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
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
@ComponentScan (
    basePackages = {"audiogen"},
    excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "audiogen.main.*")
)
public class GeneratePaperAudio implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(GeneratePaperAudio.class);

    @Autowired
    private Synchronizer syncer;

    public static void main(String[] args) throws IOException {
        SpringApplication app = new SpringApplication(GeneratePaperAudio.class);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        OptionParser parser = new OptionParser();

        parser.accepts( "n" ).withRequiredArg();
        parser.accepts( "c" ).withRequiredArg();
        OptionSet options = parser.parse(args);

        int count = 10;
        String category = "cs.AI";

        if (options.hasArgument("n")) {
            count = Integer.parseInt(options.valueOf("n").toString());
        }

        if (options.hasArgument("c")) {
            category = options.valueOf("c").toString();
        }

        LOG.info("Invoking Synchronizer with: category=" + category +", count=" + count);

        syncer.generateAudio(count, category);
        syncer.close();
    }
}
