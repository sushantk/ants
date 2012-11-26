package ants.test.stub;

import ants.core.ContextModule;
import ants.core.ContextRequest;

public class TestModuleContext extends ContextModule {
    public TestModuleContext() {
        super("test-module", null, new ContextRequest("test-request", null));
    }
}
