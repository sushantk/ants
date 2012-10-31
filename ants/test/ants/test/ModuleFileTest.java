package ants.test;

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;

import org.junit.Test;
import static org.junit.Assert.*;

import ants.Const;
import ants.ModuleFile;
import ants.api.Data;
import ants.api.ContextModule;
import ants.api.Task;
import ants.test.stub.TestModuleContext;
import ants.test.stub.TestString;
import ants.test.stub.TestTaskExecutor;

public class ModuleFileTest {
    
    @Test
    public void testSuccess() throws InterruptedException, CharacterCodingException {
        URL path = this.getClass().getResource("data/dummy");
        
        ModuleFile fetcher = new ModuleFile("fetcher", "");
        fetcher.setMimeType(new TestString(Const.mime.plain));
        fetcher.setCharSet(new TestString(Const.charSet.ascii));
        fetcher.setPath(new TestString(path.getPath()));
        
        ContextModule context = new TestModuleContext();
        Task task = fetcher.execute(context, null);
        
        TestTaskExecutor taskExecutor = new TestTaskExecutor();
        taskExecutor.submit(task);
        taskExecutor.wait(task, 5000);
        
        assertEquals("Task is done", Task.Status.DONE, task.getStatus());
        assertEquals("Result is success", Task.Result.COMPLETED, task.getResult());
        Data data = task.getData();
        assertEquals("Data mime type is plain", Const.mime.plain, data.getMimeType());
        assertEquals("Data charset is ascii", Const.charSet.ascii, data.getCharSet());
        
        ByteBuffer buffer = (ByteBuffer)data.getObject();
        buffer.rewind();
        String content = Charset.forName(data.getCharSet()).newDecoder().decode(buffer).toString();
        assertEquals("Data is as given", "dummy", content);
    }

    @Test
    public void testFail() throws InterruptedException {
        ModuleFile fetcher = new ModuleFile("fetcher", "");
        fetcher.setMimeType(new TestString(Const.mime.plain));
        fetcher.setCharSet(new TestString(Const.charSet.ascii));
        fetcher.setPath(new TestString("data/non_existent"));
        
        ContextModule context = new TestModuleContext();
        Task task = fetcher.execute(context, null);

        TestTaskExecutor taskExecutor = new TestTaskExecutor();
        taskExecutor.submit(task);

        assertEquals("Task is done", Task.Status.DONE, task.getStatus());
        assertEquals("Result is failed", Task.Result.FAILED, task.getResult());
    }
}
