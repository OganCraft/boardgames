package board.games.wizard;

/**
 * Score for one player and one game.
 */
class Score {
    /**
     * The prophecy for one (long) round.
     */
    private int prophecy;
    /**
     * Number of short round victories during one long round.
     */
    private int victories;
    /**
     * Computed score for the closed long round.
     */
    private int score;

    public Score(int prophecy) {
        this.prophecy = prophecy;
        this.victories = this.score = 0;
    }

    public int getProphecy() {
        return prophecy;
    }

    public int getVictories() {
        return victories;
    }

    public void addOneVictory() {
        this.victories++;
    }

    public int getScore() {
        return score;
    }

    public void computeScore() {
        if (prophecy == victories)
            score = 20 + 10 * victories;
        else
            score = -10 * Math.abs(prophecy - victories);
    }

    @Override
    public String toString() {
        return "Score{" +
                "prophecy=" + prophecy +
                ", victories=" + victories +
                ", score=" + score +
                '}';
    }
}
