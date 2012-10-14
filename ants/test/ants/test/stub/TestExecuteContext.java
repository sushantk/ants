package ants.test.stub;

import ants.api.ExecuteContext;
import ants.api.ModuleContext;
import ants.api.RequestContext;

public class TestExecuteContext extends ExecuteContext {
    public TestExecuteContext() {
        super(new ModuleContext("test-instance", "test-module", 
                null, new RequestContext(null), null), null);
    }
}
