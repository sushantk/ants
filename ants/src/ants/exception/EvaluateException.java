package ants.exception;

public class EvaluateException extends Exception {

    public EvaluateException(String key, String s) {
        super(key + ": " + s);
    }
    
}
