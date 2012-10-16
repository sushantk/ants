package ants.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ants.api.Data;
import ants.api.FetcherTask;
import ants.api.Task;
import ants.test.stub.TestExecuteContext;
import ants.test.stub.TestFetcher;
import ants.test.stub.TestObjectFactory;
import ants.test.stub.TestTaskExecutor;

public class FetcherTaskTest {
    
    @Test
    public void testRun() {
        Data testData = new Data(null, "");
        TestFetcher fetcher = new TestFetcher(testData, true);

        TestExecuteContext context = new TestExecuteContext();
        context.getRequestContext().setFactory(new TestObjectFactory(fetcher));
        FetcherTask task = new FetcherTask(context);

        TestTaskExecutor taskExecutor = new TestTaskExecutor();
        taskExecutor.run(task);
        
        assertEquals("Task is done", Task.Status.DONE, task.getStatus());
        assertEquals("Result is success", Task.Result.SUCCEDED, task.getResult());
        assertEquals("Data is as given", testData, task.getData());
    }

}
