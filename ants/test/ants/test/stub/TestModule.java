package ants.test.stub;

import java.util.Collection;
import java.util.Collections;

import ants.core.Configurable;
import ants.core.ContextModule;
import ants.core.Data;
import ants.core.IModule;
import ants.core.Task;
import ants.exception.ExecuteException;
import ants.ext.Module;

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
