package ants.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ants.core.Data;
import ants.core.Task;
import ants.ext.TaskModule;
import ants.test.stub.TestModuleContext;
import ants.test.stub.TestModule;
import ants.test.stub.TestObjectFactory;
import ants.test.stub.TestTaskExecutor;

public class ModuleTaskTest {
    
    @Test
    public void testRun() {
        Data testData = new Data(null, "", "");
        TestModule fetcher = new TestModule(testData);

        TestModuleContext context = new TestModuleContext();
        context.getRequestContext().setFactory(new TestObjectFactory(fetcher));
        TaskModule task = new TaskModule(context, null);

        TestTaskExecutor taskExecutor = new TestTaskExecutor();
        taskExecutor.submit(task);
        
        assertEquals("Task is done", Task.Status.DONE, task.getStatus());
        assertEquals("Result is success", Task.Result.COMPLETED, task.getResult());
        assertEquals("Data is as given", testData, task.getData());
    }

}
