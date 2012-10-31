package ants;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ants.api.Task;

/**
 * Provides foundation for task execution
 */
public class TaskExecutor implements Task.IExecutor {

    private static final Logger logger = LoggerFactory
            .getLogger(TaskExecutor.class);

    private static ThreadPoolExecutor syncTaskExecutor;
    
    private int taskCount = 0;

    /*
     * One time configuration to tune thread pools as per the need and system
     * capacity
     */
    public static void configure(int cpuTaskPoolSize, int syncIOTaskPoolSize) {

        // sync tasks have a fixed size thread pool with an unbounded queue.
        // threads start and grow up to the pool size as per the need and also
        // stay alive after the use.
        ThreadPoolExecutor syncTaskExecutor = new ThreadPoolExecutor(
                syncIOTaskPoolSize, syncIOTaskPoolSize, 0,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(
                        syncIOTaskPoolSize));
        syncTaskExecutor.allowCoreThreadTimeOut(false);
        TaskExecutor.syncTaskExecutor = syncTaskExecutor;
    }

    public TaskExecutor() {
    }

    public void submit(Task task) {
        this.submit(task, true);
    }
    
    private void submit(Task task, boolean queue) {
        logger.debug("Received task #{}: {}: ", task, ++taskCount);

        task.setExecutor(this);

        // async tasks need to tell us when they are ready to run
        if (Task.Type.SYNC == task.getType()) {
            // resulting tasks can run right away
            if(queue) {
                this.queue(task);
            } else {
                this.run(task);
            }
        }
    }

    /**
     * @see ants.api.Task.Monitor#onAsyncReady(ants.api.Task)
     */
    @Override
    public void onAsyncReady(Task task) {
        this.queue(task);
    }

    /**
     * @see ants.api.Task.Monitor#onReady(ants.api.Task, Collection<Task> next)
     */
    @Override
    public void onDone(final Task task, Collection<Task.ICallback> callbacks) {
        logger.trace("Task done: {}", task);

        LinkedList<Task> nextTasks = new LinkedList<Task>();
        for (Task.ICallback callback : callbacks) {
            // TODO: handle exception?
            Collection<Task> cbNextTasks = callback.onDone(task);
            nextTasks.addAll(cbNextTasks);
        }

        this.submitNext(nextTasks);
    }
    
    /**
     * run a task and the set of tasks collected as a result
     */
    private void run(final Task task) {
        logger.debug("Running task: {}: ", task);

        try {
            Collection<Task> next = task.run();
            this.submitNext(next);
        } catch(Exception e) {
            // Catching all exceptions for error reporting
            logger.error("Failed to run task: " + task, e);
        }
    }
    
    /**
     * Submits a set of tasks returned as a result of previous activities
     */
    private void submitNext(Collection<Task> next) {
        // Reuse the current thread to continue further execution
        // if there is only one task
        boolean queue = next.size() > 1;
        for (Task task : next) {
            this.submit(task, queue);
        }
    }
    
    private void queue(final Task task) {
        TaskExecutor.syncTaskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                TaskExecutor.this.run(task);
            }
        });
    }
}
