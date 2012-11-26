package ants.ext;

import java.util.Collection;
import java.util.Collections;

import ants.core.Data;
import ants.core.Task;

/**
 * A dummy task that can respond to the run method with a 
 * pre set data
 */
public class TaskCompleted extends Task {

    private Data runData;

    public TaskCompleted(Data data) {
        super(Task.Type.SYNC, "");
        
        this.runData = data;
    }
    
    @Override
    protected Collection<Task> runImpl() {
        this.completed(this.runData);
        return Collections.emptyList();
    }

}
