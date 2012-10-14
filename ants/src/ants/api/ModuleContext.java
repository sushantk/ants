package ants.api;

import java.util.LinkedHashMap;

import ants.api.IParams.Type;

public class ModuleContext extends Context {
    private boolean logging;

    private RequestContext requestContext;
    
    private String iid;
    private String moduleId;

    public ModuleContext(String iid, String moduleId, 
            LinkedHashMap<String, Type> params,
            RequestContext requestContext, Context parent) {
        super(parent, params);

        this.requestContext = requestContext;

        this.iid = iid;
        this.moduleId = moduleId;
        
        //TODO: Use request context to check filters
        this.logging = true;
    }

    public RequestContext getRequestContext() {
        return this.requestContext;
    }
    
    public String getInstanceId() {
        return this.iid;
    }

    public String getModuleId() {
        return this.moduleId;
    }
    
    public boolean isLogging() {
        return this.logging;
    }
    
    public String toString() {
        // TODO: chained and request
        return "context<" + this.moduleId + ", " + this.iid + ">";
    }
}
