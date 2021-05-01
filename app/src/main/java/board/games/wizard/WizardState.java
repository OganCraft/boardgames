package board.games.wizard;

import board.room.Room;
import board.user.User;

import java.util.*;

/**
 * State of the game. Number of players is variable.
 */
class WizardState {
    /**
     * Which round currently is played.
     */
    private int round;
    private Map<String, User> userMapping;
    private Map<String, List<Card>> cards;
    private Map<String, List<Score>> score;
    private Map<String, List<Boolean>> wins;
    private Map<String, Card> playedCards;  // cards thrown by users in the current round
    private Queue<Card> deck;
    private LinkedList<User> players;   // deque to help keeping the order of players during one round
    private int onTurnIndex;
    private Card trump;
    private User roundWinner, gameWinner;
    private boolean endOfRound;
    private boolean endOfGame;
    private boolean guessTime;
    private String lastAction;

    public WizardState(Room room) {
        round = 0;
        userMapping = new HashMap<>();
        cards = new HashMap<>();
        score = new HashMap<>();
        wins = new HashMap<>();
        players = new LinkedList<>(room.players());
        for (User player : room.players()) {
            userMapping.put(player.getId(), player);
            cards.put(player.getId(), new ArrayList<>());
            score.put(player.getId(), new ArrayList<>());
        }
        playedCards = new LinkedHashMap<>();

        // last winner begins the next round, for the first round the owner will start
        roundWinner = players.peekFirst();
        lastAction = "";
        prepareRoundLong();
    }

    public int getRound() {
        return round;
    }

    public Map<String, List<Card>> getCardsInHand() {
        return cards;
    }

    public Map<String, Card> getPlayedCards() {
        return playedCards;
    }

    public List<User> getPlayers() {
        return players;
    }

    public User getOnTurn() {
        return players.get(onTurnIndex);
    }

    /**
     * May be null (in the last round).
     *
     * @return the trump card
     */
    public Card getTrump() {
        return trump;
    }

    public boolean isEndOfRound() {
        return endOfRound;
    }

    public void setEndOfRound(boolean endOfRound) {
        this.endOfRound = endOfRound;
    }

    public boolean isEndOfGame() {
        return endOfGame;
    }

    public void setEndOfGame(boolean endOfGame) {
        this.endOfGame = endOfGame;
    }

    public boolean isGuessTime() {
        return guessTime;
    }

    public void setGuessTime(boolean guessTime) {
        this.guessTime = guessTime;
    }

    public User getRoundWinner() {
        return roundWinner;
    }

    public User getUser(String userId) {
        return userMapping.get(userId);
    }

    public void setRoundWinner(User roundWinner) {
        this.roundWinner = roundWinner;
    }

    public User getGameWinner() {
        return gameWinner;
    }

    public void setGameWinner(User gameWinner) {
        this.gameWinner = gameWinner;
    }

    public void nextOnTurn() {
        onTurnIndex++;
    }

    public String getLastAction() {
        return lastAction;
    }

    public void setLastAction(String lastAction) {
        this.lastAction = lastAction;
    }

    private void prepareDeck() {
        LinkedList<Card> c = new LinkedList<>(Card.allCards());
        Collections.shuffle(c, new Random(System.currentTimeMillis()));
        deck = c;
    }

    /**
     * Long round contains as much short rounds as what number of cards the player has on the beginning
     * of the long round. Long round begins by wins number guessing.
     */
    public void prepareRoundShort() {
        endOfRound = endOfGame = false;
        playedCards.clear();

        while (!roundWinner.equals(players.peekFirst())) {
            // sort players to have last winner as the first one on turn when new round begins
            players.addLast(players.removeFirst());
        }

        onTurnIndex = 0;
        roundWinner = null;
    }

    public void prepareRoundLong() {
        round++;
        endOfRound = endOfGame = false;
        playedCards.clear();
        prepareDeck();

        // take cards from the deck for each player
        for (Map.Entry<String, List<Card>> entry : cards.entrySet()) {
            List<Card> playerCards = entry.getValue();
            // should be empty, but just for safety
            if (!playerCards.isEmpty())
                throw new IllegalStateException("not empty! player: " + entry.getKey() + ", cards: " + playerCards);

            for (int i = 0; i < round; i++) {
                playerCards.add(deck.remove());
            }
        }

        if (deck.isEmpty())
            trump = null;
        else
            trump = deck.remove();

        while (!roundWinner.equals(players.peekFirst())) {
            // sort players to have last winner as the first one on turn when new round begins
            players.addLast(players.removeFirst());
        }

        onTurnIndex = 0;
        guessTime = true;
    }
}
