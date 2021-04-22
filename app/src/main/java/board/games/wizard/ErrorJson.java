package board.games.wizard;

public class ErrorJson extends EventJson {
    private String message;

    public ErrorJson(String message) {
        super("error");
        this.message = message;
    }
}
