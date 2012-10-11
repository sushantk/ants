package ants.api;

import java.util.LinkedHashMap;

import ants.api.IParams.Type;

public class RequestContext extends Context {

    public RequestContext(LinkedHashMap<String, Type> params) {
        super(null, params);
    }

    public boolean isLogging() {
        // TODO: use filters
        return true;
    }
}
