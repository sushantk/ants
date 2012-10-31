package ants.api;

import java.util.Collection;
import java.util.Collections;

public class CallbackCopy implements Task.ICallback {

    Task target;

    public CallbackCopy(Task task) {
        this.target = task;
    }
    
    @Override
    public Collection<Task> onDone(final Task task) {
        if(Task.Result.COMPLETED == task.getResult()) {
            target.completed(task.getData());
        } else {
            target.failed(task.getData(), null);
        }
        
        return Collections.emptyList();
    }

}
