package kr.co.ultari.board.exceptions;

@SuppressWarnings("serial")
public class IsWrongPasswordException extends Exception {

    private Throwable cause;

    public IsWrongPasswordException(String message) {
        super(message);
    }

    public IsWrongPasswordException(Throwable t) {
        super(t.getMessage());
        this.cause = t;
    }

    public Throwable getCause() {
        return this.cause;
    }
}