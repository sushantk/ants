package ants.api;

import java.util.LinkedList;

/**
 * The most basic unit of activity
 */
public abstract class Task {

    static public enum Status {
        DONE, NOT_DONE
    }

    static public enum Result {
        FAILED, SUCCEDED, TIMEDOUT
    }

    static public enum Type {
        CPU, SYNC_IO, ASYNC_IO
    }

    /**
     * Callback to send the task completion notification to one
     * or more listeners.
     */
    public interface Callback {
        /**
         * The listener can return one more tasks as a result to execute
         * after the callback
         */
        Iterable<Task> onComplete(Task task);
    }

    private Type type;
    private Status status = Status.NOT_DONE;
    private Result result = Result.FAILED;
    private Object userData;
    private Data data;

    private LinkedList<Callback> callbacks = new LinkedList<Callback>();

    public Task(Type type, Object userData) {
        this.userData = userData;
        this.type = type;
    }

    public Object getUserData() {
        return this.userData;
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

    synchronized public void setData(Data data, Result result) {
        this.status = Status.DONE;

        this.data = data;
        this.result = result;

        // let the monitor know that the async task has finished fetching
        if ((Type.ASYNC_IO == this.type) && (null != this.asyncMonitor)) {
            this.asyncMonitor.onAsyncDataReady(this);
            this.asyncMonitor = null;
        }
    }

    public abstract Iterable<Task> run();

    public Iterable<Callback> getCallbacks() {
        return this.callbacks;
    }

    public void addCallback(Callback callback) {
        this.callbacks.add(callback);
    }

    public String toString() {
        return "Task<" + userData + ">";
    }

    /**
     * Used by the task manager to monitor async task activity
     */
    public interface AsyncMonitor {
        /**
         * Notify the monitor when the async task has finished fetching
         * the data 
         */
        void onAsyncDataReady(Task task);
    }

    private AsyncMonitor asyncMonitor;

    /**
     * if the data has already arrived, we will notify the monitor
     * right away, else save it for future notification in setData
     */
    synchronized public void setAsyncMonitor(AsyncMonitor monitor) {
        if (Status.DONE == this.status) {
            monitor.onAsyncDataReady(this);
        } else {
            this.asyncMonitor = monitor;
        }
    }
}
