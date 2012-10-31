package ants.exception;

public class InvalidStateException extends RuntimeException {
    
    public InvalidStateException(String key, String message) {
        super(key + ": " + message);
    }
}
