package ants.test.stub;

import java.util.Collection;
import java.util.Collections;

import ants.api.Configurable;
import ants.api.Data;
import ants.api.ExecuteContext;
import ants.api.IFetcher;
import ants.api.Task;

public class TestFetcher extends Configurable
                         implements IFetcher {

    Data data;
    boolean returnSuccess;

    public TestFetcher(Data data, boolean returnSuccess) {
        super("fetcher", "");
        
        this.data = data;
        this.returnSuccess = returnSuccess;
    }

    @Override
    public Task fetch(ExecuteContext context) {
        return new Task(Task.Type.SYNC) {
            protected Collection<Task> runImpl() {
                this.setData(TestFetcher.this.data, TestFetcher.this.returnSuccess ? Result.SUCCEDED : Result.FAILED);
                return Collections.emptyList();
            }
        };
    }

}
