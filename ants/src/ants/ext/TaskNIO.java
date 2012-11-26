package ants.ext;

import java.nio.channels.CompletionHandler;
import java.util.Collection;
import java.util.Collections;

import ants.core.Data;
import ants.core.Task;
import ants.core.Task.Type;
import ants.exception.ExecuteException;

public class TaskNIO extends Task implements CompletionHandler<Integer, Object> {

    private Data asyncData;
    private Throwable asyncException;

    public TaskNIO(String key) {
        super(Type.ASYNC, key);
    }

    public void setAsyncData(Data data) {
        this.asyncData = data;
    }

    public void setAsyncException(Throwable e) {
        this.asyncException = e;
    }

    @Override
    protected Collection<Task> runImpl() {
        if (null != this.asyncException) {
            super.failed(null, new ExecuteException(this.toString(), "Failed to execute", this.asyncException));
        } else if(null == this.asyncData) {
            super.failed(null, new ExecuteException(this.toString(), "Internal error, failed to set data"));
        }
        
        super.completed(this.asyncData);
        return Collections.emptyList();
    }

    @Override
    public void completed(Integer result, Object attachment) {
        this.ready();
        
    }
    
    @Override
    public void failed(Throwable exc, Object attachment) {
        this.setAsyncException(exc);
        this.ready();
    }

}
