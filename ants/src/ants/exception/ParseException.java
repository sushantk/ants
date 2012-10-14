package ants.exception;

public class ParseException extends Exception {

    public ParseException(String s) {
        super(s);
    }

    public ParseException(String s, Throwable cause) {
        super(s, cause);
    }
}
