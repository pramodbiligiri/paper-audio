package audiogen.main;

import audiogen.workflows.PaperPipelineWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;

@Component
@ComponentScan(
    basePackages = {"audiogen"},
    excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "audiogen.main.*")
)
public class PaperPipelineStarter implements CommandLineRunner {

    @Autowired
    private WorkflowClient workflowClient;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(PaperPipelineStarter.class);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue("paper-main")
                .build();

        PaperPipelineWorkflow workflow = workflowClient.newWorkflowStub(
                PaperPipelineWorkflow.class, options);

        workflow.runPipeline();
    }
}
