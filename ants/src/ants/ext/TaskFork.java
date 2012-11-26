package ants.ext;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import ants.core.Data;
import ants.core.Task;
import ants.exception.ExecuteException;

/**
 * A task that runs multiple other tasks 
 */
public class TaskFork extends Task implements Task.ICallback {

    public interface ICallback {
        Data join(Map<String, Task> tasks) throws ExecuteException;
    }

    Map<String, Task> tasks;
    ICallback runCallback;
    int runTaskCount = 0;

    public TaskFork(String key, Map<String, Task> tasks, ICallback callback) {
        super(Task.Type.SYNC, key);

        this.tasks = tasks;
        this.runCallback = callback;
    }
    
    @Override
    protected Collection<Task> runImpl() {
        Collection<Task> tasks = this.tasks.values();
        for(Task task : tasks) {
            task.addCallback(this);
        }
        
        return tasks;
    }

    @Override
    public Collection<Task> onDone(Task task) {
        if(++this.runTaskCount == this.tasks.size()) {
            try {
                Data data = this.runCallback.join(this.tasks);
                this.completed(data);
            } catch(ExecuteException e) {
                this.failed(null, e);
            }            
        }

        return Collections.emptyList();
    }

}
