package board.games;

public enum Games {
    SHIPS("Ships", 2, 2, "/ships");


    private final String name;
    private final int min;
    private final int max;
    private final String path;

    Games(String name, int min, int max, String path) {
        this.name = name;
        this.min = min;
        this.max = max;
        this.path = path;
    }

    /**
     * Name of a game.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Minimal number of players.
     *
     * @return the number
     */
    public int minPlayers() {
        return min;
    }

    /**
     * Maximal number of players.
     *
     * @return the number
     */
    public int maxPlayers() {
        return max;
    }

    /**
     * Location of the game (part of URL).
     *
     * @return servlet path to the game
     */
    public String gamePath() {
        return path;
    }
}
