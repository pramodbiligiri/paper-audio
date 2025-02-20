package audiogen.main;

import audiogen.tts.InputCleaner;
import audiogen.tts.TextToSpeech;
import audiogen.tts.TtsResult;
import io.bitken.tts.model.entity.converter.IAudioFile;
import io.bitken.tts.model.entity.converter.LocalBlobStorageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;
import java.io.IOException;

//@SpringBootApplication
@Component
@ComponentScan(
    basePackages = {"audiogen"},
    excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "audiogen.main.*")
)
public class TtsBasic implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TtsBasic.class);

    @Autowired
    private TextToSpeech tts;

    @Autowired
    private LocalBlobStorageHandler storage;

    public static void main(String[] args) throws IOException {
        SpringApplication app = new SpringApplication(TtsBasic.class);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        InputCleaner inputCleaner = new InputCleaner();

        for (String input : inputs) {
            LOG.info("Making tts request ...");
            TtsResult ttsResult = tts.toSpeech(inputCleaner.cleanInput(input));
            LOG.info("Finished tts request");

            IAudioFile file = storage.newFile(ttsResult.getAudioBytes());
            LOG.info("Stored into: " + file.getFullPath());
        }
    }

    private static String[] inputs = new String[] {
        "a mean mismatch of 4.73$\\pm$2.15%. The $20$ $\\gamma$ \\em ``stability, accuracy, and real-time"
    };
}
