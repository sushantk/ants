package ants.test.stub;

import ants.api.ExecuteContext;
import ants.api.ModuleContext;
import ants.api.RequestContext;

public class TestExecuteContext extends ExecuteContext {
    public TestExecuteContext() {
        super("test-object", null, new ModuleContext("test-module", 
                null, new RequestContext("test-request", null), null));
    }
}
