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
     * The real number of wins in one (long) round.
     */
    private int wins;
    /**
     * Computed score for the closed long round.
     */
    private int score;

    public Score(int prophecy) {
        this.prophecy = prophecy;
        this.wins = this.score = 0;
    }

    public int getProphecy() {
        return prophecy;
    }

    public void setProphecy(int prophecy) {
        this.prophecy = prophecy;
    }

    public int getWins() {
        return wins;
    }

    public void addOneWin() {
        this.wins++;
    }

    public int getScore() {
        return score;
    }

    public void computeScore() {
        if (prophecy == wins)
            score = 20 + 10 * wins;
        else
            score = -10 * Math.abs(prophecy - wins);
    }
}
