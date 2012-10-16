package ants.api;

import java.util.LinkedHashMap;

import ants.api.IParams.Type;

public class ModuleContext extends Context {
    
    private boolean logging;
    private RequestContext requestContext;

    public ModuleContext(String id, LinkedHashMap<String, Type> params,
            RequestContext requestContext, Context parent) {
        super(id, params, (null == parent ? requestContext : parent));

        this.requestContext = requestContext;        
        //TODO: Use request context to check filters
        this.logging = true;
    }

    public RequestContext getRequestContext() {
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
