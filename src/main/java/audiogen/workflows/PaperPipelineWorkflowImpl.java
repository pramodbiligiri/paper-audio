package audiogen.workflows;

import audiogen.activities.PaperPipelineActivities;
import audiogen.arxiv.parse.ParseResult;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class PaperPipelineWorkflowImpl implements PaperPipelineWorkflow {

    private final PaperPipelineActivities activities = Workflow.newActivityStub(
            PaperPipelineActivities.class,
            ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofMinutes(10))
                .setScheduleToCloseTimeout(Duration.ofMinutes(15))
                .build());

    @Override
    public void runPipeline() {
        activities.fetchArxivData();
        ParseResult parseResult = activities.processFetchedData();
        activities.generateAudio(parseResult);
    }
}