package ants.api;

import java.util.LinkedHashMap;

import ants.api.IParams.Type;

public class RequestContext extends Context {
    
    IObjectFactory factory;

    public RequestContext(String id, LinkedHashMap<String, Type> params) {
        super(id, params, null);
    }

    public IObjectFactory getFactory() {
        return this.factory;
    }

    public void setFactory(IObjectFactory factory) {
        this.factory = factory;
    }

    public boolean isLogging() {
        // TODO: use filters
        return true;
    }
    
    public String toString() {
        return "request<" + this.getId() + ">";
    }
}
