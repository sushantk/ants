package ants.test;

import org.junit.Test;
import static org.junit.Assert.*;

import ants.ModuleEcho;
import ants.core.Const;
import ants.core.Data;
import ants.core.Task;
import ants.test.stub.TestString;

public class ModuleEchoTest {
    
    @Test
    public void testSuccess() {
        String testStr = "Hello World";

        ModuleEcho fetcher = new ModuleEcho("fetcher", "");
        fetcher.setString(new TestString(testStr));
        
        Task task = Util.executeModule(fetcher);
        
        assertEquals("Task is done", Task.Status.DONE, task.getStatus());
        assertEquals("Result is success", Task.Result.COMPLETED, task.getResult());
        Data data = task.getData();
        assertEquals("Data mime type is plain", Const.mime.plain, data.getMimeType());
        assertEquals("Data is as given", testStr, data.getData());
    }

    @Test
    public void testFail() {
        ModuleEcho fetcher = new ModuleEcho("fetcher", "");
        fetcher.setString(new TestString());
        
        Task task = Util.executeModule(fetcher);
        
        assertEquals("Task is done", Task.Status.DONE, task.getStatus());
        assertEquals("Result is failed", Task.Result.FAILED, task.getResult());
    }
}
