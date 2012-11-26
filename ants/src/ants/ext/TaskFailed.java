package ants.ext;

import java.util.Collection;
import java.util.Collections;

import ants.core.Data;
import ants.core.Task;

/**
 * A dummy task that can respond to the run method with an 
 * exception and data if any
 */
public class TaskFailed extends Task {

    private Data runData;
    private Throwable runException;

    public TaskFailed(Throwable e) {
        super(Task.Type.SYNC, "");
        
        this.runException = e;
    }

    public TaskFailed(Data data, Throwable e) {
        super(Task.Type.SYNC, "");
        
        this.runData = data;
        this.runException = e;
    }
    
    @Override
    protected Collection<Task> runImpl() {
        this.failed(this.runData, this.runException);
        return Collections.emptyList();
    }

}
