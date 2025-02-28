package audiogen.activities;

import audiogen.arxiv.parse.ParseResult;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface PaperPipelineActivities {
    @ActivityMethod
    void fetchArxivData();

    @ActivityMethod
    ParseResult processFetchedData();

    @ActivityMethod
    void generateAudio(ParseResult parseResult);
}