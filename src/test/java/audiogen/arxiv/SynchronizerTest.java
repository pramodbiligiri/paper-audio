package audiogen.arxiv;

import audiogen.config.AppConfig;
import audiogen.tts.Synchronizer;
import audiogen.tts.TextToSpeech;
import audiogen.tts.TtsResult;
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import io.bitken.tts.model.entity.PaperAudio;
import io.bitken.tts.model.entity.PaperCategory;
import io.bitken.tts.model.entity.PaperData;
import io.bitken.tts.model.entity.PaperTtsTask;
import io.bitken.tts.model.entity.converter.BlobStorageHandler;
import io.bitken.tts.repo.PaperAudioRepo;
import io.bitken.tts.repo.PaperDataRepo;
import io.bitken.tts.repo.PaperTtsTaskRepo;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest (classes = {AppConfig.class, Synchronizer.class})
public class SynchronizerTest {

    private static final Logger LOG = LoggerFactory.getLogger(SynchronizerTest.class);

    private static final byte[] AUDIO_FILE_DUMMY_BYTES = {65, 66, 67, 68};

    @Autowired
    PaperDataRepo paperDataRepo;

    @Autowired
    PaperAudioRepo paperAudioRepo;

    @Autowired
    Synchronizer synchronizer;

    @Autowired
    PaperTtsTaskRepo ttsTaskRepo;

    @Autowired
    BlobStorageHandler storageHandler;

    @MockBean
    TextToSpeech tts;

    @BeforeEach
    public void beforeEach() throws IOException {
        Mockito.when(tts.toSpeech(anyString())).
            thenReturn(
                new TtsResult(
                    AUDIO_FILE_DUMMY_BYTES,
                    AudioConfig.newBuilder().
                        setAudioEncoding(AudioEncoding.MP3).
                        build()
                )
        );
    }

    @AfterEach
    public void afterEach() {
        ttsTaskRepo.deleteAll();
        paperAudioRepo.deleteAll();
        paperDataRepo.deleteAll();
    }

    @Test
    public void testSync1() throws IOException {
        initTestSync1();
        List<PaperData> papers = paperDataRepo.findLatestAIPapersWithoutAudio();
        Assertions.assertEquals(3, papers.size());

        synchronizer.generateAudio(papers, Integer.MAX_VALUE, "cs.AI");

        Assertions.assertEquals(0, paperDataRepo.findPapersWithoutAudio().size());

        List<PaperTtsTask> tasks = ttsTaskRepo.findAll();
        Assertions.assertEquals(3, tasks.size());
        for (PaperTtsTask task : tasks) {
            Assertions.assertEquals(1, task.getStatus());
        }
    }

    private void initTestSync1() throws IOException {
        PaperData pd1 = new PaperData();
        pd1.setTitle("Paper 1");
        pd1.addCategory(new PaperCategory().setCategory("cs.AI"));
        pd1 = paperDataRepo.save(pd1);

        PaperAudio pa1 = new PaperAudio();
        pa1.setPaper(pd1);
        pa1.setAudio(storageHandler.newFile(AUDIO_FILE_DUMMY_BYTES));
        pa1 = paperAudioRepo.save(pa1);
        pd1.setAudio(pa1);

        PaperData pd2 = new PaperData();
        pd2.setTitle("Paper 2");
        pd2.setPubDate(new Timestamp(new Date().getTime()));
        pd2.addCategory(new PaperCategory().setCategory("cs.AI"));
        pd2 = paperDataRepo.save(pd2);

        PaperData pd3 = new PaperData();
        pd3.setTitle("Paper 3");
        pd3.setPubDate(new Timestamp(new Date().getTime()));
        pd3.addCategory(new PaperCategory().setCategory("cs.AI"));
        pd3 = paperDataRepo.save(pd3);

        PaperData pd4 = new PaperData();
        pd4.setTitle("Paper 4");
        pd4.setPubDate(new Timestamp(new Date().getTime()));
        pd4.addCategory(new PaperCategory().setCategory("cs.AI"));
        pd4 = paperDataRepo.save(pd4);
    }

}
