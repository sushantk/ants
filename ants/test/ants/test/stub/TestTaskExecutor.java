package ants.test.stub;

import java.util.Collection;

import ants.api.Task;

public class TestTaskExecutor implements Task.IMonitor {

    public void run(final Task task) {
        task.setMonitor(this);
        Collection<Task> next = task.run();
        this.runNext(next);
    }

    @Override
    public void onDone(final Task task, Collection<Task> next) {
        this.runNext(next);
    }

    private void runNext(Collection<Task> next) {
        for (Task task : next) {
            this.run(task);
        }
    }

}
