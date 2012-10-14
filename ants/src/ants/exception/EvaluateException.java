package ants.exception;

import ants.api.Configurable;
import ants.api.Context;

public class EvaluateException extends Exception {

    Context context;
    Configurable object;

    public EvaluateException(String s, Context context, Configurable object) {
        super(s);
        
        this.context = context;
        this.object = object;
    }
    
    public String toString() {
        return object.toContextString(this.context) + ": " + this.getMessage();
    }
}
