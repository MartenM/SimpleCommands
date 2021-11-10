package nl.martenm.simplecommands.arguments;

/**
 * Exception that should be thrown when parsing is not possible.
 * Can also be thrown when parsing gives no result.
 */
public class ParseFailedException extends Exception {

    public ParseFailedException(Exception ex) {
        super(ex);
    }

    public ParseFailedException(String message) {
        super(message);
    }
}
