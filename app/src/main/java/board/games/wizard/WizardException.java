package board.games.wizard;

/**
 * The exception is thrown when an error message should be sent to the user.
 */
public class WizardException extends RuntimeException {
    private String message;

    public WizardException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
