package ants;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Paths;
import java.util.Collection;

import ants.annotation.ConfigurableMethod;
import ants.core.Configurable;
import ants.core.ContextModule;
import ants.core.Data;
import ants.core.IString;
import ants.core.Task;
import ants.exception.EvaluateException;
import ants.exception.ExecuteException;
import ants.ext.Module;
import ants.ext.TaskFailed;
import ants.ext.TaskNIO;

public class ModuleFile extends Module {

    private IString path;
    private IString mime;
    private IString charSet;

    public ModuleFile(String tagName, String id) {
        super(tagName, id);
    }

    @ConfigurableMethod(required = true)
    public void setPath(Configurable value) {
        this.path = IString.class.cast(value);
    }

    @ConfigurableMethod(required = true, defaultClass = "ants.StringLiteral")
    public void setMimeType(Configurable value) {
        this.mime = IString.class.cast(value);
    }

    @ConfigurableMethod(defaultClass = "ants.StringLiteral")
    public void setCharSet(Configurable value) {
        this.charSet = IString.class.cast(value);
    }

    @Override
    public Task execute(ContextModule context, Data input) {
        
        String filePath = null;
        try {
            filePath = this.path.getValue(context);
            String mime = this.mime.getValue(context);
            String charSet = "";
            if (null != this.charSet) {
                charSet = this.charSet.getValue(context);
            }

            AsynchronousFileChannel channel = AsynchronousFileChannel
                    .open(Paths.get(filePath));
            // TODO: long to int conversion check
            ByteBuffer dst = ByteBuffer.allocate((int) channel.size());
            Data data = new Data(dst, mime, charSet);
            data.setUri(filePath);
            
            TaskNIO task = new TaskFile(data, this.toContextString(context, filePath));
            channel.read(dst, 0, null, task);
            return task;
        } catch (EvaluateException e) {
            return new TaskFailed(new ExecuteException(this.toContextString(context),
                    "Failed to evaluate connfiguration", e));
        } catch (IOException e) {
            return new TaskFailed(new ExecuteException(this.toContextString(context, filePath),
                    "Failed to initiate file read", e));
        }
    }
    
    private static class TaskFile extends TaskNIO {
        
        private Data runData;

        public TaskFile(Data data, String key) {
            super(key);
            
            this.runData = data;
        }

        @Override
        protected Collection<Task> runImpl() {
            ByteBuffer buffer = (ByteBuffer)this.runData.getData();
            buffer.rewind();
            this.setAsyncData(runData);
            return super.runImpl();
        }
    }

}
