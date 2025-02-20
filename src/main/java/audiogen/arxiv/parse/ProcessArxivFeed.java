package audiogen.arxiv.parse;

import audiogen.arxiv.records.*;
import audiogen.arxiv.records.Record;
import io.bitken.tts.model.entity.ArxivOai;
import io.bitken.tts.model.entity.PaperCategory;
import io.bitken.tts.model.entity.PaperData;
import io.bitken.tts.repo.ArxivOaiRepo;
import io.bitken.tts.repo.PaperCategoryRepo;
import io.bitken.tts.repo.PaperDataRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class ProcessArxivFeed {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessArxivFeed.class);

    private final ArxivOaiRepo oaiRepo;
    private final PaperDataRepo paperDataRepo;
    private final PaperCategoryRepo paperCategoryRepo;
    private final ParseArxivRaw parser;

    public ProcessArxivFeed(ArxivOaiRepo oaiRepo, PaperDataRepo paperDataRepo, PaperCategoryRepo paperCategoryRepo) {
        this.oaiRepo = oaiRepo;
        this.paperDataRepo = paperDataRepo;
        this.paperCategoryRepo = paperCategoryRepo;
        parser = new ParseArxivRaw();
    }

    public ParseResult processLatestBatch() throws IOException {
        Long maxBatchId = oaiRepo.findMaxBatchId();
        LOG.info("Max batch_id: " + maxBatchId);

        List<ArxivOai> oais = oaiRepo.findByBatchId(maxBatchId);
        LOG.info("Found " + oais.size()  +" OAI XML entries in batch_id: " + maxBatchId);

        ParseResult parseResult = new ParseResult(0, 0);

        for (ArxivOai oai : oais) {
            LOG.info("Processing OAI XML with id: " + oai.getId());
            OaiFeed feed = parser.parse(oai.getOaiXml());

            if (feed == null) {
                LOG.error("Could not parse the XML. Skipping OAI entry");
                continue;
            }

            processFeed(feed).ifPresent(parseResult::add);
        }

        LOG.info("Finished processing OAI XML entries");

        return parseResult;

    }

    private Optional<ParseResult> processFeed(OaiFeed feed) {
        ListRecords listRecords = feed.getListRecords();
        if (listRecords == null) {
            LOG.error("Could not get ListRecords object from feed. Skipping this feed object.");
            return Optional.empty();
        }

        List<Record> records = listRecords.getRecords();
        if (records == null) {
            LOG.error("Could not extract list of Record-s from ListRecord. Skipping this feed object.");
            return Optional.empty();
        }

        LOG.info("No. of records to process in feed: " + records.size());

        int processed = 0, skipped = 0;
        for (Record record : records) {
            Metadata metadata = record.getMetadata();
            ArxivRaw arxivRaw = metadata.getArxivRaw();
            String arxivId = arxivRaw.getArxivId();

            if (paperDataRepo.checkExists(arxivId)) {
                LOG.warn("Skipping as paper_data record with arxiv_id='" + arxivId + "' already exists");

                skipped++;
                continue;
            }

            LOG.info("Processing record with arxivId: " + arxivId + " ...");

            PaperData paperData = initPaperData(arxivRaw, arxivId);
            paperData = paperDataRepo.save(paperData);

            saveCategories(arxivRaw, paperData);

            processed++;
            LOG.info("finished processing record with arxivId: " + arxivId);
        }

        LOG.info("Finished processing feed. Total records: " + records.size() + ". No. of items processed: "
                + processed + ". No. of items skipped: " + skipped);

        if (records.size() != (processed + skipped)) {
            LOG.warn("This should never happen! Record Count (" + records.size()
                + ") is not equal to (processed + skipped): " + (processed + skipped));
        }

        return Optional.of(new ParseResult(records.size(), skipped));
    }

    private PaperData initPaperData(ArxivRaw arxivRaw, String arxivId) {
        PaperData paperData = new PaperData();

        paperData.setAbstractt(arxivRaw.getAbstractt());
        paperData.setTitle(arxivRaw.getTitle());
        paperData.setAuthors(arxivRaw.getAuthors());
        paperData.setArxivId(arxivId);
        paperData.setLink("https://arxiv.org/abs/" + arxivId);
        
        Timestamp pubDate = arxivRaw.getPubDate().map(v -> new Timestamp(v.getTime())).orElse(null);
        paperData.setPubDate(pubDate);

        return paperData;
    }

    private void saveCategories(ArxivRaw arxivRaw, PaperData paperData) {
        for (String cat : arxivRaw.getCategories()) {
            PaperCategory paperCat = new PaperCategory();
            paperCat.setPaperData(paperData);
            paperCat.setCategory(cat);
            paperCategoryRepo.save(paperCat);
        }
    }
}
