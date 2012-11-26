package ants.ext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import ants.core.Data;
import ants.core.Task;
import ants.exception.ExecuteException;

/**
 * A task that runs multiple other tasks in sequence. Data from the last
 * task is used after filtering 
 */
public class TaskRelay extends Task implements Task.ICallback {
    
    public interface ICallback {
        Data filter(String taskId, Task task) throws ExecuteException;
    }

    LinkedHashMap<String, Task> tasks;
    ICallback runCallback;
    Iterator<Entry<String, Task>> runTaskIterator;
    Entry<String, Task> runTaskEntry;

    public TaskRelay(String key, LinkedHashMap<String, Task> tasks, ICallback callback) {
        super(Task.Type.SYNC, key);
        
        this.tasks = tasks;
        this.runCallback = callback;
    }
    
    @Override
    protected Collection<Task> runImpl() {
        this.runTaskIterator = this.tasks.entrySet().iterator();
        
        this.runTaskEntry = this.runTaskIterator.next();
        Task firstTask = this.runTaskEntry.getValue();
        firstTask.addCallback(this);
        
        ArrayList<Task> firstTaskList = new ArrayList<>();
        firstTaskList.add(firstTask);
        return firstTaskList;
    }

    @Override
    public Collection<Task> onDone(Task task) {
        Data data = null;
        try {
            data = this.runCallback.filter(this.runTaskEntry.getKey(), task);
        } catch(ExecuteException e) {
            this.failed(null, e);
        }

        if(this.runTaskIterator.hasNext()) {
            this.runTaskEntry = this.runTaskIterator.next();
            Task nextTask = this.runTaskEntry.getValue();
            nextTask.addCallback(this);
            
            ArrayList<Task> nextTaskList = new ArrayList<>();
            nextTaskList.add(nextTask);
            return nextTaskList;
        } else {
            this.completed(data);
        }
        
        return Collections.emptyList();
    }

}
