package ants.exception;

public class ExecuteException extends Exception {

    public ExecuteException(String key, String message) {
        super(key + ": " + message);
    }    

    public ExecuteException(String key, String message, Throwable cause) {
        super(key + ": " + message, cause);
    }    
}
