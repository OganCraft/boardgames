package board.games;

public enum Games {
    SHIPS("Lodě", 2, 2, "/ships"),
    CHOCO_WITCH("Čarodějnice", 2, 6, "/chocolate"),
    WIZARD("Wizard", 3, 6, "/wizard"),
    COVER_NAMES("Krycí jména", 2, 10, "/names");

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
     * Enum name. Just to be accessible via getter.
     *
     * @return Enum.name()
     */
    public String getId() {
        return name();
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
