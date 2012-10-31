package ants.api;

import java.util.Collection;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ants.exception.InvalidStateException;

/**
 * The most basic unit of activity
 */
public abstract class Task {

    static public enum Status {
        NOT_RUN, READY, RUNNING, DONE
    }

    static public enum Result {
        FAILED, COMPLETED, TIMEDOUT
    }

    static public enum Type {
        SYNC, ASYNC
    }

    private static final Logger logger = LoggerFactory.getLogger(Task.class);

    /**
     * Used by the task manager to monitor task activity and run the next set of
     * tasks of returned by the callbacks
     */
    public interface IExecutor {
        
        /**
         * Notify the executor when an async task is ready to run
         */
        void onAsyncReady(final Task task);
        
        /**
         * Notify the monitor when the task has finished fetching the data
         */
        void onDone(final Task task, Collection<ICallback> calbacks);
    }

    /**
     * Callback to send the task completion notification to one or more
     * listeners.
     */
    public interface ICallback {
        /**
         * The listener can return one more tasks as a result to execute after
         * the callback
         */
        Collection<Task> onDone(final Task task);
    }

    private Type type;
    private String key;
    private Status status = Status.NOT_RUN;
    private Result result = Result.FAILED;
    private Data data;

    private LinkedList<ICallback> callbacks = new LinkedList<ICallback>();
    private IExecutor executor;

    public Task(Type type, String key) {
        this.type = type;
        this.key = key;

        if(Type.SYNC == type) {
            this.status = Status.READY;
        }
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
    
    public void addCallback(ICallback callback) {
        this.callbacks.add(callback);
    }

    synchronized public void ready() throws InvalidStateException {
        if (Status.NOT_RUN != this.status) {
            throw new InvalidStateException(this.toString(), "Task is not in NOT_RUN state");
        }
        
        this.status = Status.READY;
        if(null != this.executor) {
            this.executor.onAsyncReady(this);
        }
    }

    public void completed(Data data) throws InvalidStateException {
        if (Status.RUNNING != this.status) {
            throw new InvalidStateException(this.toString(), "Task is not run yet");
        }

        this.done(data, Result.COMPLETED);
    }

    public void failed(Data data, Throwable e) throws InvalidStateException {
        if (Status.RUNNING != this.status) {
            throw new InvalidStateException(this.toString(), "Task is not run yet");
        }

        logger.error("Failed to run task: " + this, e);
        this.done(data, Result.FAILED);
    }

    synchronized public void setExecutor(IExecutor executor) {
        this.executor = executor;
        
        if((Type.ASYNC == this.type) && (Status.READY == this.status)) {
            this.executor.onAsyncReady(this);
        }
    }

    protected abstract Collection<Task> runImpl();

    public final Collection<Task> run() {
        if (Status.READY != this.status) {
            throw new InvalidStateException(this.toString(), "Task is not ready to run");
        }

        this.status = Status.RUNNING;
        // TODO: handle exception?
        Collection<Task> next = this.runImpl();

        // If it is a sync task, not done and no further tasks are returned,
        // it must have failed
        if((Type.ASYNC != this.type) && (Status.DONE != this.status) && (next.isEmpty())) {
            this.completed(null);
        }

        return next;
    }

    public String toString() {
        return this.key + "/task<" + this.getClass().getName() + ", " + this.type + ">";
    }
    
    private void done(Data data, Result result) {
        this.status = Status.DONE;

        this.data = data;
        this.result = result;
        this.executor.onDone(this, this.callbacks);
    }

}
