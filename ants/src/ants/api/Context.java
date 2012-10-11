package ants.api;

import java.util.LinkedHashMap;

import ants.api.IParams.Type;

public abstract class Context {

    private Context parent;    
    private LinkedHashMap<String, Type> params;

    public Context(Context parent, LinkedHashMap<String, Type> params) {
        this.parent = parent;
        this.params = params;
    }

    public Context getParent() {
        return this.parent;
    }

    public Type get(String key) {
        return (null == this.params ? null : this.params.get(key));
    }
    
    public abstract boolean isLogging();
}
