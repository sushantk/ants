package ants.api;

import java.util.Collection;
import java.util.LinkedList;

import ants.exception.InvalidStateException;

/**
 * The most basic unit of activity
 */
public abstract class Task {

    static public enum Status {
        NOT_RUN, RUNNING, DONE
    }

    static public enum Result {
        FAILED, SUCCEDED, TIMEDOUT
    }

    static public enum Type {
        CPU, SYNC_IO, ASYNC_IO
    }

    /**
     * Used by the task manager to monitor task activity and run the next set of
     * tasks of returned by the callbacks
     */
    public interface Monitor {
        /**
         * Notify the monitor when the task has finished fetching the data
         */
        void onDone(Task task, Collection<Task> next);
    }

    /**
     * Callback to send the task completion notification to one or more
     * listeners.
     */
    public interface Callback {
        /**
         * The listener can return one more tasks as a result to execute after
         * the callback
         */
        Collection<Task> onDone(Task task);
    }

    private Type type;
    private Status status = Status.NOT_RUN;
    private Result result = Result.FAILED;
    private Data data;

    private LinkedList<Callback> callbacks = new LinkedList<Callback>();
    private Monitor monitor;

    public Task(Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    public Status getStatus() {
        return this.status;
    }

    public Result getResult() {
        return this.result;
    }

    public Data getData() {
        return this.data;
    }

    public void addCallback(Callback callback) {
        this.callbacks.add(callback);
    }

    public void setData(Data data, Result result) throws InvalidStateException {
        if (Status.RUNNING != this.status) {
            throw new InvalidStateException("Task is not run yet: " + this);
        }

        this.data = data;
        this.result = result;
        this.status = Status.DONE;

        this.complete();
    }

    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    protected abstract Collection<Task> runImpl();

    public final Collection<Task> run() {
        if (Status.NOT_RUN != this.status) {
            throw new InvalidStateException("Task is already run: " + this);
        }

        this.status = Status.RUNNING;
        Collection<Task> next = this.runImpl();

        // If it is a sync task, not done and no further tasks are returned,
        // it must have failed
        if((Type.ASYNC_IO != this.type) && (Status.DONE != this.status) && (next.isEmpty())) {
            this.setData(null, Result.FAILED);    
        }

        return next;
    }

    public String toString() {
        return "<" + super.toString() + ", " + this.type + ">";
    }

    /**
     * Notify callbacks, collect tasks and finally notify the monitor with the
     * tasks collected from the callbacks
     */
    private void complete() {
        LinkedList<Task> nextTasks = new LinkedList<Task>();
        for (Task.Callback callback : this.callbacks) {
            Collection<Task> cbNextTasks = callback.onDone(this);
            nextTasks.addAll(cbNextTasks);
        }

        this.monitor.onDone(this, nextTasks);
    }
}
