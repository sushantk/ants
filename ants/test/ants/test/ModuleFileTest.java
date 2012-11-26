package ants.test;

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;

import org.junit.Test;
import static org.junit.Assert.*;

import ants.ModuleFile;
import ants.core.Const;
import ants.core.Data;
import ants.core.Task;
import ants.test.stub.TestString;

public class ModuleFileTest {
    
    @Test
    public void testSuccess() throws InterruptedException, CharacterCodingException {
        URL path = this.getClass().getResource("data/dummy");
        
        ModuleFile fetcher = new ModuleFile("fetcher", "");
        fetcher.setMimeType(new TestString(Const.mime.plain));
        fetcher.setCharSet(new TestString(Const.charSet.ascii));
        fetcher.setPath(new TestString(path.getPath()));
        
        Task task = Util.executeModule(fetcher, 1000);
        
        assertEquals("Task is done", Task.Status.DONE, task.getStatus());
        assertEquals("Result is success", Task.Result.COMPLETED, task.getResult());
        Data data = task.getData();
        assertEquals("Data mime type is plain", Const.mime.plain, data.getMimeType());
        assertEquals("Data charset is ascii", Const.charSet.ascii, data.getCharSet());
        
        ByteBuffer buffer = (ByteBuffer)data.getData();
        String content = Charset.forName(data.getCharSet()).newDecoder().decode(buffer).toString();
        assertEquals("Data is as given", "dummy", content);
    }

    @Test
    public void testFail() {
        ModuleFile fetcher = new ModuleFile("fetcher", "");
        fetcher.setMimeType(new TestString(Const.mime.plain));
        fetcher.setCharSet(new TestString(Const.charSet.ascii));
        fetcher.setPath(new TestString("data/non_existent"));
        
        Task task = Util.executeModule(fetcher);

        assertEquals("Task is done", Task.Status.DONE, task.getStatus());
        assertEquals("Result is failed", Task.Result.FAILED, task.getResult());
    }

}
