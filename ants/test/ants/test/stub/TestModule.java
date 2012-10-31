package ants.test.stub;

import java.util.Collection;
import java.util.Collections;

import ants.api.Configurable;
import ants.api.Data;
import ants.api.IModule;
import ants.api.ContextModule;
import ants.api.Module;
import ants.api.Task;
import ants.exception.ExecuteException;

public class TestModule extends Module implements IModule {

    Data data;
    boolean returnSuccess;

    public TestModule() {
        super("module", "test-module");

        this.returnSuccess = false;
    }

    public TestModule(Data data) {
        super("module", "test-module");

        this.data = data;
        this.returnSuccess = true;
    }

    @Override
    public Task execute(final ContextModule context, Data input) {
        return new Task(Task.Type.SYNC, "") {
            protected Collection<Task> runImpl() {
                if (TestModule.this.returnSuccess) {
                    this.completed(TestModule.this.data);
                } else {
                    this.failed(null, new ExecuteException(
                            TestModule.this.toContextString(context),
                            "Returning failure"));
                }

                return Collections.emptyList();
            }
        };
    }

}
