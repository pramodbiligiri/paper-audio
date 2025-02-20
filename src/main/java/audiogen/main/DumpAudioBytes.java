package audiogen.main;

import io.bitken.tts.model.entity.PaperAudio;
import io.bitken.tts.repo.PaperAudioRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Component
@ComponentScan (
    basePackages = {"audiogen"},
    excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "audiogen.main.*")
)
public class DumpAudioBytes implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DumpAudioBytes.class);

    @Autowired
    private PaperAudioRepo paperAudioRepo;

    public static void main(String[] args) throws IOException {
        SpringApplication app = new SpringApplication(DumpAudioBytes.class);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info("Dumping audio files from database ...");
        List<PaperAudio> audios = paperAudioRepo.findAll();
        int i = 0;
        for (PaperAudio audio : audios) {
            i++;
            byte[] bytes = audio.getAudio().getData();
            OutputStream out = new FileOutputStream("output-" + audio.getPaperData().getId() + ".mp3");
            out.write(bytes);
            out.close();
            if (i == 5) {
                break;
            }
        }
        LOG.info("Finished dumping audio files from database");
    }
}
