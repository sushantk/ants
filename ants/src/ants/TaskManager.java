package ants;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ants.api.Task;

/**
 * Provides foundation for task execution
 */
public class TaskManager implements Task.AsyncMonitor {

    static final Logger logger = LoggerFactory.getLogger(TaskManager.class);

    static ThreadPoolExecutor cpuTaskExecutor;
    static ThreadPoolExecutor syncIOTaskExecutor;

    /*
     * One time configuration to tune thread pools as per the need and system
     * capacity
     */
    public static void configure(int cpuTaskPoolSize, int syncIOTaskPoolSize) {
        // cpu tasks have a fixed size pre-started thread pool with an unbounded
        // queue. it is recommended that the pool size be a little more than the 
        // number of available cores in the system
        ThreadPoolExecutor cpuTaskExecutor = new ThreadPoolExecutor(
                cpuTaskPoolSize, cpuTaskPoolSize, 0, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(cpuTaskPoolSize));
        cpuTaskExecutor.allowCoreThreadTimeOut(false);
        cpuTaskExecutor.prestartAllCoreThreads();
        TaskManager.cpuTaskExecutor = cpuTaskExecutor;

        // sync io tasks have a fixed size thread pool with an unbounded queue.
        // threads start and grow up to the pool size as per the need and also
        // stay alive after the use.
        ThreadPoolExecutor syncIOTaskExecutor = new ThreadPoolExecutor(
                syncIOTaskPoolSize, syncIOTaskPoolSize, 0,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(
                        syncIOTaskPoolSize));
        syncIOTaskExecutor.allowCoreThreadTimeOut(false);
        TaskManager.syncIOTaskExecutor = syncIOTaskExecutor;
    }

    boolean logging;

    public TaskManager(boolean logging) {
        this.logging = logging;
    }

    public void run(final Task task) {
        final TaskManager self = this;

        switch (task.getType()) {
        case CPU: {
            TaskManager.cpuTaskExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    Iterable<Task> next = task.run();
                    self.complete(task, next);
                }
            });
        }
            break;
        case SYNC_IO: {
            TaskManager.syncIOTaskExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    Iterable<Task> next = task.run();
                    self.complete(task, next);
                }
            });
        }
            break;
        case ASYNC_IO: {
            task.setAsyncMonitor(this);
        }
            break;
        }
    }

    /**
     * @see ants.api.Task.AsyncMonitor#onAsyncDataReady(ants.api.Task)
     */
    @Override
    public void onAsyncDataReady(final Task task) {
        // thread pool is not used to run the async io task, as it is already
        // done fetching data. it is supposedly going to advice for any further
        // activity via returned tasks
        Iterable<Task> next = task.run();
        this.complete(task, next);
    }

    /**
     * finalize the task after completing its run
     */
    private void complete(Task task, Iterable<Task> nextTasks) {
        // run the new set of tasks returned by the completed task
        for (Task next : nextTasks) {
            this.run(next);
        }

        // notify the callbacks and run the new set of tasks returned
        // by the callback
        Iterable<Task.Callback> callbacks = task.getCallbacks();
        for (Task.Callback callback : callbacks) {
            Iterable<Task> cbNextTasks = callback.onComplete(task);
            for (Task next : cbNextTasks) {
                this.run(next);
            }
        }
    }
}
