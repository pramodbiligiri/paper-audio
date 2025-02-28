package audiogen.activities;

import audiogen.arxiv.fetch.ArxivOaiFetcher;
import audiogen.arxiv.parse.ParseResult;
import audiogen.arxiv.parse.ProcessArxivFeed;
import audiogen.tts.Synchronizer;
import io.bitken.tts.model.domain.CategoryInfo;
import io.temporal.activity.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;

@Service
public class PaperPipelineActivitiesImpl implements PaperPipelineActivities {

    @Autowired
    private ArxivOaiFetcher arxivOaiFetcher;

    @Autowired
    private ProcessArxivFeed processArxivFeed;

    @Autowired
    private Synchronizer syncer;

    private static final Logger LOG = LoggerFactory.getLogger(PaperPipelineActivitiesImpl.class);

    @Override
    public void fetchArxivData() {
        try {
            arxivOaiFetcher.fetch();
        } catch (ParseException | IOException e) {
            throw Activity.wrap(e);
        }
    }

    @Override
    public ParseResult processFetchedData() {
        try {
            return processArxivFeed.processLatestBatch();
        } catch (Exception e) {
            throw Activity.wrap(e);
        }
    }

    @Override
    public void generateAudio(ParseResult parseResult) {
        if (!parseResult.hasNoNewPapers()) {
            for (CategoryInfo cat : CategoryInfo.values()) {
                for (String arxivCat : cat.getArxivCats()) {
                    try {
                        syncer.generateAudio(1, arxivCat);
                    } catch (Exception e) {
                        LOG.error("Error generating audio for category: " + arxivCat, e);
                    }
                }
            }
        }
        syncer.close();
    }
}