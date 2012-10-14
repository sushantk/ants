package ants.test;

import org.junit.Test;
import static org.junit.Assert.*;

import ants.Const;
import ants.FetcherEcho;
import ants.api.Data;
import ants.api.ExecuteContext;
import ants.api.Task;
import ants.test.stub.TestExecuteContext;
import ants.test.stub.TestString;
import ants.test.stub.TestTaskExecutor;

public class FetcherEchoTest {
    
    @Test
    public void testSuccess() {
        String testStr = "Hello World";

        FetcherEcho fetcher = new FetcherEcho("fetcher", "");
        fetcher.setString(new TestString(testStr, true));
        
        ExecuteContext context = new TestExecuteContext();
        Task task = fetcher.fetch(context);
        task.setMonitor(new TestTaskExecutor());
        task.run();
        
        assertEquals("Task is done", Task.Status.DONE, task.getStatus());
        assertEquals("Result is success", Task.Result.SUCCEDED, task.getResult());
        Data data = task.getData();
        assertEquals("Data mime type is plain", Const.mime.plain, data.getMimeType());
        assertEquals("Data is as given", testStr, data.getObject());
    }

    @Test
    public void testFail() {
        FetcherEcho fetcher = new FetcherEcho("fetcher", "");
        fetcher.setString(new TestString(null, false));
        
        ExecuteContext context = new TestExecuteContext();
        Task task = fetcher.fetch(context);
        task.setMonitor(new TestTaskExecutor());
        task.run();
        
        assertEquals("Task is done", Task.Status.DONE, task.getStatus());
        assertEquals("Result is failed", Task.Result.FAILED, task.getResult());
    }
}
