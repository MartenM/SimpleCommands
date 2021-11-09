package nl.martenm.simplecommands.arguments;

/**
 * Exception that should be thrown when parsing is not possible.
 * Can also be thrown when parsing gives no result.
 */
public class ParseFailedException extends Exception {

    private Exception exception;
    public ParseFailedException(Exception ex) {
        this.exception = ex;
    }

    public ParseFailedException(String message) {
        this.exception = new Exception(message);
    }

    public Exception getException() {
        return exception;
    }
}
