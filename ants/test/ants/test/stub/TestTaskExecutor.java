package ants.test.stub;

import java.util.Collection;
import java.util.LinkedList;

import ants.core.Task;

public class TestTaskExecutor implements Task.IExecutor {
    
    public void submit(Task task) {
        task.setExecutor(this);
        
        if(Task.Type.SYNC == task.getType()) {
            this.run(task);
        }
    }
    
    public void wait(Task task, long timeout) throws InterruptedException {
        synchronized (task) {
            task.wait(timeout);            
        }
    }

    @Override
    public void onAsyncReady(Task task) {
        this.run(task);
    }

    @Override
    public void onDone(final Task task, Collection<Task.ICallback> callbacks) {
        LinkedList<Task> nextTasks = new LinkedList<Task>();
        for (Task.ICallback callback : callbacks) {
            Collection<Task> cbNextTasks = callback.onDone(task);
            nextTasks.addAll(cbNextTasks);
        }

        this.submitNext(nextTasks);
        
        synchronized (task) {
            task.notifyAll();            
        }
    }

    public void run(final Task task) {
        Collection<Task> next = task.run();
        this.submitNext(next);
    }
    
    private void submitNext(Collection<Task> next) {
        for (Task task : next) {
            this.submit(task);
        }
    }

}
