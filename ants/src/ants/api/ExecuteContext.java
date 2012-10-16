package ants.api;

import java.util.LinkedHashMap;

import ants.api.IParams.Type;

public class ExecuteContext extends Context {

    private ModuleContext moduleContext;

    public ExecuteContext(String id, LinkedHashMap<String, Type> params,
            ModuleContext parent) {
        super(id, params, parent);

        this.moduleContext = parent;
    }

    public ExecuteContext(String id, LinkedHashMap<String, Type> params,
            ExecuteContext parent) {
        super(id, params, parent);

        this.moduleContext = parent.getModuleContext();
    }

    public RequestContext getRequestContext() {
        return this.moduleContext.getRequestContext();
    }

    public ModuleContext getModuleContext() {
        return this.moduleContext;
    }

    @Override
    public boolean isLogging() {
        return this.moduleContext.isLogging();
    }

    public String toString() {
        return this.getParent().toString() + "/context<" + this.getId() + ">";
    }
}
