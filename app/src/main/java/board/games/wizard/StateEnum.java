package board.games.wizard;

/**
 * List of exclusive states.
 */
enum StateEnum {
    GAME("game"),
    END_OF_ROUND("end-of-round"),
    END_OF_GAME("end-of-game"),
    PROPHECY_TIME("prophecy-time"),
    AWAITING_START_OF_ROUND("awaiting-start"),
    ;

    private String alias;

    StateEnum(String alias) {
        this.alias = alias;
    }

    @Override
    public String toString() {
        return alias;
    }
}
