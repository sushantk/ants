package ants.test;

import org.junit.Test;
import static org.junit.Assert.*;

import ants.Const;
import ants.ModuleEcho;
import ants.api.Data;
import ants.api.ContextModule;
import ants.api.Task;
import ants.test.stub.TestModuleContext;
import ants.test.stub.TestString;
import ants.test.stub.TestTaskExecutor;

public class ModuleEchoTest {
    
    @Test
    public void testSuccess() {
        String testStr = "Hello World";

        ModuleEcho fetcher = new ModuleEcho("fetcher", "");
        fetcher.setString(new TestString(testStr));
        
        ContextModule context = new TestModuleContext();
        Task task = fetcher.execute(context, null);
        TestTaskExecutor taskExecutor = new TestTaskExecutor();
        taskExecutor.submit(task);
        
        assertEquals("Task is done", Task.Status.DONE, task.getStatus());
        assertEquals("Result is success", Task.Result.COMPLETED, task.getResult());
        Data data = task.getData();
        assertEquals("Data mime type is plain", Const.mime.plain, data.getMimeType());
        assertEquals("Data is as given", testStr, data.getObject());
    }

    @Test
    public void testFail() {
        ModuleEcho fetcher = new ModuleEcho("fetcher", "");
        fetcher.setString(new TestString());
        
        ContextModule context = new TestModuleContext();
        Task task = fetcher.execute(context, null);
        TestTaskExecutor taskExecutor = new TestTaskExecutor();
        taskExecutor.submit(task);
        
        assertEquals("Task is done", Task.Status.DONE, task.getStatus());
        assertEquals("Result is failed", Task.Result.FAILED, task.getResult());
    }
}
