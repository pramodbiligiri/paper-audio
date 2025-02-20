package audiogen.tts;

import io.bitken.tts.model.entity.PaperAudio;
import io.bitken.tts.model.entity.PaperData;
import io.bitken.tts.model.entity.PaperTtsTask;
import io.bitken.tts.model.entity.converter.BlobStorageHandler;
import io.bitken.tts.model.entity.converter.IAudioFile;
import io.bitken.tts.repo.PaperAudioRepo;
import io.bitken.tts.repo.PaperDataRepo;
import io.bitken.tts.repo.PaperTtsTaskRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class Synchronizer {

    private static final Logger LOG = LoggerFactory.getLogger(Synchronizer.class);

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private PaperAudioRepo paperAudioRepo;

    @Autowired
    private PaperDataRepo paperDataRepo;

    @Autowired
    private PaperTtsTaskRepo ttsTaskRepo;

    @Autowired
    private TextToSpeech tts;

    @Autowired
    private BlobStorageHandler storageHandler;

    private final PaperToTtsInput paperToTtsInput;

    public Synchronizer() {
        paperToTtsInput = new PaperToTtsInput();
    }

    public void generateAudio(int paperCount, String category) throws IOException {
        LOG.info("Generating audio for category: " + category + " with paper count: " + paperCount);

        List<PaperData> papers = paperDataRepo.findLatestPapersWithoutAudioInCategories(Arrays.asList(category));

        int runPaperCount = generateAudio(papers, paperCount, category);

        LOG.info("Generated audio for category: " + category + ". No. of papers processed: " + runPaperCount);
    }

    public int generateAudio(List<PaperData> papers, int paperCount, String category) throws IOException {
        int processedCount = 0;

        for (PaperData p : papers) {
            LOG.info("Converting to audio: Paper id: " + p.getId() + ", category: " + category +", pubDate: " +
                    sdf.format(p.getPubDate()) + ", Title: " + p.getTitle());

            PaperAudio paperAudio = convertToAudio(p, tts);

            LOG.info("Finished converting to audio: Paper id: " + p.getId() +
                    ". Audio id: " + paperAudio.getId());

            processedCount++;
            if (processedCount == paperCount) {
                LOG.info("Finished " + paperCount + " papers. Exiting loop");
                break;
            }
        }

        return processedCount;
    }

    private PaperAudio convertToAudio(PaperData p, TextToSpeech tts) throws IOException {
        PaperTtsTask task = initPaperTaskEntry(p);

        String ttsInput = paperToTtsInput.asTtsInput(p);
        TtsResult ttsResult = tts.toSpeech(ttsInput);

        PaperAudio paperAudio = save(ttsResult, p);

        task = updatePaperTaskEntry(task, paperAudio);

        return paperAudio;
    }

    private PaperAudio save(TtsResult ttsResult, PaperData p) throws IOException {
        Optional<PaperData> paperOpt = paperDataRepo.findById(p.getId());
        if (paperOpt.isEmpty()) {
            LOG.warn("Not saving audio because no corresponding paper found for paperId: " + p.getId());
            return null; // TODO: Change to Optional
        }

        PaperData paper = paperOpt.get();

        PaperAudio pa = new PaperAudio();
        pa.setPaper(paper);

        IAudioFile audioFile = storageHandler.newFile(ttsResult.getAudioBytes());
        pa.setAudio(audioFile);

        int duration = getDuration(audioFile, p);
        pa.setDuration(duration);

        pa.setCreateTime(new Timestamp(new Date().getTime()));

        return paperAudioRepo.save(pa);
    }

    private int getDuration(IAudioFile audioFile, PaperData p) throws IOException {
        Optional<Duration> durationOpt = audioFile.getDuration();

        if (durationOpt.isEmpty()) {
            LOG.warn("Setting duration ZERO for paperId: " + p.getId());
            return 0;
        }

        return (int)durationOpt.get().getSeconds();
    }

    private PaperTtsTask initPaperTaskEntry(PaperData p) {
        PaperTtsTask task = new PaperTtsTask();
        task.setStatus(0);
        task.setStartTime(new Timestamp(System.currentTimeMillis()));
        task.setPaperId(p.getId());

        return ttsTaskRepo.save(task);
    }

    private PaperTtsTask updatePaperTaskEntry(PaperTtsTask task, PaperAudio pa) {
        // TODO: What if we fail at recording task status??
        // Is the task execution idempotent?
        task.setPaperAudioId(pa.getId());
        task.setStatus(1);
        task.setEndTime(new Timestamp(System.currentTimeMillis()));

        return ttsTaskRepo.save(task);
    }

    public void close() {
        if (tts != null) {
            tts.close();
        }

    }
}
