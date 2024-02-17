package farewellworkflow;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.common.RetryOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

import java.time.Duration;

public class Starter {
    public static void main(String[] args) throws Exception {

        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

        WorkflowClient client = WorkflowClient.newInstance(service);

        RetryOptions retryOptions = RetryOptions.newBuilder()
                .setInitialInterval(Duration.ofSeconds(15))   // first retry will occur after 15 seconds
                .setBackoffCoefficient(2.0)                   // double the delay after each retry
                .setMaximumInterval(Duration.ofSeconds(60))   // up to a maximum delay of 60 seconds
                .setMaximumAttempts(3)                      // fail the Activity after 100 attempts
                .build();

        WorkflowOptions options = WorkflowOptions.newBuilder()
                    .setWorkflowId("greeting-workflow")
                    .setTaskQueue("greeting-tasks")
                    .setWorkflowExecutionTimeout(Duration.ofSeconds(60))
                    .setWorkflowRunTimeout(Duration.ofSeconds(30))
                    .setWorkflowTaskTimeout(Duration.ofSeconds(45))
                    .setRetryOptions(retryOptions)
                    .build();
       
        GreetingWorkflow workflow = client.newWorkflowStub(GreetingWorkflow.class, options);

        String greeting = workflow.greetSomeone(args[0]);

        String workflowId = WorkflowStub.fromTyped(workflow).getExecution().getWorkflowId();

        System.out.println(workflowId + " " + greeting);
        System.exit(0);
    }
}
