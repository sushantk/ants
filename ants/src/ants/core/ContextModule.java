package ants.core;

import java.util.LinkedHashMap;

import ants.core.IParams.Type;

public class ContextModule extends Context {
    
    private boolean logging;
    private ContextRequest requestContext;

    public ContextModule(String id, LinkedHashMap<String, Type> params,
            ContextModule parent) {
        super(id, params, parent);

        this.requestContext = parent.getRequestContext();        
        //TODO: Use request context to check filters
        this.logging = true;
    }

    public ContextModule(String id, LinkedHashMap<String, Type> params,
            ContextRequest parent) {
        super(id, params, parent);

        this.requestContext = parent;        
        //TODO: Use request context to check filters
        this.logging = true;
    }

    public ContextRequest getRequestContext() {
        return this.requestContext;
    }
    
    public String getModuleId() {
        return this.getId();
    }
    
    public boolean isLogging() {
        return this.logging;
    }
    
    public String toString() {
        return this.getParent() + "/module<" + this.getId() + ">";
    }
}
