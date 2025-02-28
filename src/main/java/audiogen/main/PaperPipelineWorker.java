package audiogen.main;

import audiogen.activities.PaperPipelineActivities;
import audiogen.workflows.PaperPipelineWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
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
public class PaperPipelineWorker implements CommandLineRunner {

    @Autowired
    private WorkflowClient workflowClient;

    @Autowired
    private PaperPipelineActivities activities;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(PaperPipelineWorker.class);

        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        WorkerFactory factory = WorkerFactory.newInstance(workflowClient);
        Worker worker = factory.newWorker("paper-main");

        worker.registerWorkflowImplementationTypes(PaperPipelineWorkflowImpl.class);
        worker.registerActivitiesImplementations(activities);

        factory.start();
    }
}
