package ants.test.stub;

import ants.api.ContextModule;
import ants.api.ContextRequest;

public class TestModuleContext extends ContextModule {
    public TestModuleContext() {
        super("test-module", null, new ContextRequest("test-request", null));
    }
}
