package io.kestra.core.runners;

import io.kestra.core.models.executions.Execution;
import io.kestra.core.models.flows.State;
import io.kestra.core.queues.QueueFactoryInterface;
import io.kestra.core.queues.QueueInterface;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RetryTest extends AbstractMemoryRunnerTest {
    @Inject
    @Named(QueueFactoryInterface.EXECUTION_NAMED)
    protected QueueInterface<Execution> executionQueue;

    @Test
    void retrySuccess() throws TimeoutException {
        Execution execution = runnerUtils.runOne("io.kestra.tests", "retry-success");

        assertThat(execution.getState().getCurrent(), is(State.Type.WARNING));
        assertThat(execution.getTaskRunList(), hasSize(1));
        assertThat(execution.getTaskRunList().get(0).getAttempts(), hasSize(5));
    }

    @Test
    void retryFailed() throws TimeoutException {
        List<Execution> executions = new ArrayList<>();
        executionQueue.receive(executions::add);

        Execution execution = runnerUtils.runOne("io.kestra.tests", "retry-failed");

        assertThat(execution.getTaskRunList(), hasSize(2));
        assertThat(execution.getTaskRunList().get(0).getAttempts(), hasSize(5));

        // be sure attempts are available on the queue
        // we cannot know the exact number of executions, but we should have at least 15 of them
        assertThat(executions.size(), greaterThan(15));
        assertThat(executions.get(8).getTaskRunList().get(0).getAttempts().size(), is(3));
    }
}
