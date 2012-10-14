package ants.test.stub;

import java.util.Collection;

import ants.api.Task;

public class TestTaskExecutor implements Task.Monitor {

    private Collection<Task> next;

    @Override
    public void onDone(Task task, Collection<Task> next) {
        this.next = next;
    }

    public Collection<Task> getNext() {
        return next;
    }
}
