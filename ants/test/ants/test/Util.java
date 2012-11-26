package ants.test;

import ants.core.ContextModule;
import ants.core.IModule;
import ants.core.Task;
import ants.test.stub.TestModuleContext;
import ants.test.stub.TestTaskExecutor;

public class Util {

    public static Task executeModule(IModule module) {
        try {
            return Util.executeModule(module, 0);
        } catch (InterruptedException e) {
            // ignored
            return null;
        }
    }

    public static Task executeModule(IModule module, int timeout) throws InterruptedException {
        ContextModule context = new TestModuleContext();
        Task task = module.execute(context, null);
        TestTaskExecutor taskExecutor = new TestTaskExecutor();
        taskExecutor.submit(task);
        if(timeout > 0) {
            taskExecutor.wait(task, timeout);
        }
        return task;
    }
}
