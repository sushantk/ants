package ants.exception;

import ants.api.Configurable;
import ants.api.Context;

public class ObjectEvaluateException extends Exception {

    Context context;
    Configurable object;

    public ObjectEvaluateException(String s, Context context, Configurable object) {
        super(s);
        
        this.context = context;
        this.object = object;
    }
    
    public String toContextString() {
        return this.context + "/" + object.toTagString() + ": " + this.getMessage();
    }
}
