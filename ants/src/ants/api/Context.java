package ants.api;

import java.util.LinkedHashMap;

import ants.api.IParams.Type;

public abstract class Context {
 
    private String id;
    private LinkedHashMap<String, Type> params;
    private Context parent;

    public Context(String id, LinkedHashMap<String, Type> params, Context parent) {
        this.id = id;
        this.params = params;
        this.parent = parent;
    }

    public String getId() {
        return this.id;
    }

    public Context getParent() {
        return this.parent;
    }

    public Type get(String key) {
        return (null == this.params ? null : this.params.get(key));
    }
    
    public abstract boolean isLogging();
}
