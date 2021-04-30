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
    private int round, roundCounter;    // roundCounter - how many cards are used in the current round
    private Map<String, User> userMapping;
    private Map<String, List<Card>> cards;
    private Map<String, List<Score>> score;
    private Map<String, List<Boolean>> wins;
    private Map<String, Card> playedCards;  // cards thrown by users in the current round
    private Queue<Card> deck;
    private LinkedList<User> players;   // here leveraged its Deque interface
    private LinkedList<User> oneRoundPlayers;   // deque to help keeping the order of players during one round
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
        oneRoundPlayers = new LinkedList<>(players);
        for (User player : room.players()) {
            userMapping.put(player.getId(), player);
            cards.put(player.getId(), new ArrayList<>());
            score.put(player.getId(), new ArrayList<>());
        }
        playedCards = new LinkedHashMap<>();

        // last winner begins the next round, for the first round the owner will start
        roundWinner = players.peekFirst();
        lastAction = "";
        prepareNextRound();
    }

    public int getRound() {
        return round;
    }

    public int getRoundCounter() {
        return roundCounter;
    }

    public void decreaseRoundCounter() {
        roundCounter--;
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
        return oneRoundPlayers.peekFirst();
    }

    /**
     * May be null (in the last round).
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
        oneRoundPlayers.addLast(oneRoundPlayers.removeFirst());
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

    public void prepareNextRound() {
        roundCounter = ++round;
        endOfRound = endOfGame = false;
        playedCards.clear();
        prepareDeck();

        // take cards from the deck for each player
        for (Map.Entry<String, List<Card>> entry : cards.entrySet()) {
            List<Card> cards = entry.getValue();
            // should be empty, but just for safety
            if (!cards.isEmpty())
                throw new IllegalStateException("not empty! player: " + entry.getKey() + ", cards: " + cards);

            for (int i = 0; i < round; i++) {
                cards.add(deck.remove());
            }
        }

        if (deck.isEmpty())
            trump = null;
        else
            trump = deck.remove();

        while (!roundWinner.equals(getOnTurn())) {
            // sort players to have last winner as the first one on turn when new round begins
            nextOnTurn();
        }

        roundWinner = null;
        guessTime = true;
    }
}
