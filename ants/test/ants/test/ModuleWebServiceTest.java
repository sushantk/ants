package ants.test;

import org.junit.Test;

import com.ning.http.client.Response;

import static org.junit.Assert.*;

import ants.ModuleWebService;
import ants.core.Const;
import ants.core.Data;
import ants.core.Task;
import ants.test.stub.TestString;

public class ModuleWebServiceTest {
    
    @Test
    public void testSuccess() throws InterruptedException {
        ModuleWebService fetcher = new ModuleWebService("fetcher", "");
        fetcher.setUrl(new TestString("http://www.google.com"));
        
        Task task = Util.executeModule(fetcher, 5000);
        
        assertEquals("Task is done", Task.Status.DONE, task.getStatus());
        assertEquals("Result is success", Task.Result.COMPLETED, task.getResult());
        Data data = task.getData();
        assertTrue("Data mime type is object", data.getMimeType().startsWith(Const.mime.object));
        Response response = (Response)data.getData();
        assertEquals("Response is 200", 200, response.getStatusCode());
    }

    @Test
    public void testFailConfiguration() {
        ModuleWebService fetcher = new ModuleWebService("fetcher", "");
        fetcher.setUrl(new TestString());
        
        Task task = Util.executeModule(fetcher);
        
        assertEquals("Task is done", Task.Status.DONE, task.getStatus());
        assertEquals("Result is failed", Task.Result.FAILED, task.getResult());
    }
    
    @Test
    public void testFailBadUrl() {
        ModuleWebService fetcher = new ModuleWebService("fetcher", "");
        fetcher.setUrl(new TestString("bad://www.host.com"));
        
        Task task = Util.executeModule(fetcher);

        assertEquals("Task is done", Task.Status.DONE, task.getStatus());
        assertEquals("Result is failed", Task.Result.FAILED, task.getResult());
    }

}
