package ants.api;

public class Context {
    private boolean logging;

    private RequestContext requestContext;
    
    private String iid;
    private String moduleId;

    public Context(RequestContext requestContext, String iid, String moduleId) {
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
        return "Context<" + this.moduleId + ", " + this.iid + ">";
    }
}
