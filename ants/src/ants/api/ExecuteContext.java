package ants.api;

import java.util.LinkedHashMap;

import ants.api.IParams.Type;

public class ExecuteContext extends Context {

    private ModuleContext moduleContext;
    
    public ExecuteContext(ModuleContext moduleContext, LinkedHashMap<String, Type> params) {
        super(moduleContext, params);

        this.moduleContext = moduleContext;
    }

    public ExecuteContext(ExecuteContext executeContext, LinkedHashMap<String, Type> params) {
        super(executeContext, params);

        this.moduleContext = executeContext.getModuleContext();
    }

    public ModuleContext getModuleContext() {
        return this.moduleContext;
    }

    @Override
    public boolean isLogging() {
        return this.moduleContext.isLogging();
    }
    
    public String toString() {
        return this.moduleContext.toString();
    }
}
