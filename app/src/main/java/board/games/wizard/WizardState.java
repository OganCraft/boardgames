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
    private Map<String, Card> actualRoundCards;
    private Queue<Card> deck;
    private LinkedList<User> players;   // here leveraged its Deque interface
    private Card trump;
    private User onTurn, roundWinner, gameWinner;
    private boolean endOfRound;
    private boolean endOfGame;

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
        actualRoundCards = new LinkedHashMap<>();

        // last winner begins the next round, for the first round the owner will start
        roundWinner = onTurn = players.peekFirst();
        prepareNextRound();
    }

    public int getRound() {
        return round;
    }

    public Map<String, List<Card>> getCardsInHand() {
        return cards;
    }

    public Map<String, Card> getActualRoundCards() {
        return actualRoundCards;
    }

    public List<User> getPlayers() {
        return players;
    }

    public User getOnTurn() {
        return onTurn;
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
        players.addLast(players.removeFirst());
        onTurn = players.peekFirst();
    }

    private void prepareDeck() {
        LinkedList<Card> c = new LinkedList<>(Card.allCards());
        Collections.shuffle(c, new Random(System.currentTimeMillis()));
        deck = c;
    }

    public void prepareNextRound() {
        round++;
        endOfRound = endOfGame = false;
        actualRoundCards.clear();
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

        while (!roundWinner.equals(onTurn)) {
            // sort players to have last winner as the first one on turn when the game begins
            nextOnTurn();
        }
    }
}
