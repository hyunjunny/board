package kr.co.ultari.board.exceptions;

@SuppressWarnings("serial")
public class UserNotFoundException extends Exception {

    private Throwable cause;

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(Throwable t) {
        super(t.getMessage());
        this.cause = t;
    }

    public Throwable getCause() {
        return this.cause;
    }
}