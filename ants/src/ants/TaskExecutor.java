package ants;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ants.api.Task;

/**
 * Provides foundation for task execution
 */
public class TaskExecutor implements Task.IMonitor {

    private static final Logger logger = LoggerFactory
            .getLogger(TaskExecutor.class);

    private static ThreadPoolExecutor syncTaskExecutor;

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

    boolean logging;

    public TaskExecutor(boolean logging) {
        this.logging = logging;
    }

    public void queue(final Task task) {
        logger.debug("Queue task: {}: ", task);

        task.setMonitor(this);

        final TaskExecutor self = this;
        switch (task.getType()) {
        case SYNC: {
            TaskExecutor.syncTaskExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    self.run(task);
                }
            });
        }
            break;
        case ASYNC: {
            // async tasks don't need a managed thread pool, they should have
            // their own pool as required
            self.run(task);
        }
            break;
        }
    }

    /**
     * @see ants.api.Task.Monitor#onReady(ants.api.Task, Collection<Task> next)
     */
    @Override
    public void onDone(final Task task, Collection<Task> next) {
        logger.trace("Task done: {}, next: ", task, next);
        this.runNext(next);
    }

    /**
     * run a task and the set of tasks collected as a result
     */
    private void run(final Task task) {
        logger.debug("Running task: {}: ", task);

        try {
            Collection<Task> next = task.run();
            this.runNext(next);
        } catch(Exception e) {
            // Catching all exceptions for error reporting
            logger.error("Failed to run task: " + task, e);
        }
    }

    /**
     * Run a set of tasks returned as a result of previous activities
     */
    private void runNext(Collection<Task> next) {
        for (Task task : next) {
            this.queue(task);
        }
    }

}
