package ants.test;

import ants.api.ExecuteContext;
import ants.api.ModuleContext;
import ants.api.RequestContext;

public class Util {
    public static ExecuteContext createExecuteContext() {
        return new ExecuteContext(
            new ModuleContext("test-instance", "test-module", 
                null, new RequestContext(null), null), null);
    }

}
