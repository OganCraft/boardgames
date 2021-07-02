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
    private final Map<User, List<Card>> cards;
    private final Map<User, List<Score>> score;
    private final Map<User, Card> playedCards;  // cards thrown by users in the current round
    private Queue<Card> deck;
    private final LinkedList<User> players;   // deque to help keeping the order of players during one round
    private int onTurnIndex;
    private Card trump;
    private User roundWinner, gameWinner;
    private StateEnum currentState;

    public WizardState(Room room) {
        round = 0;
        cards = new HashMap<>();
        score = new HashMap<>();
        players = new LinkedList<>(room.players());
        for (User player : room.players()) {
            cards.put(player, new ArrayList<>());
            score.put(player, new ArrayList<>());
        }
        playedCards = new LinkedHashMap<>();

        // last winner begins the next round, for the first round the owner will start
        roundWinner = players.peekFirst();
        prepareRoundLong();
    }

    public int getRound() {
        return round;
    }

    public Map<User, List<Card>> getCardsInHand() {
        return cards;
    }

    public Map<User, Card> getPlayedCards() {
        return playedCards;
    }

    public List<User> getPlayers() {
        return players;
    }

    public Map<User, List<Score>> getScore() {
        return score;
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

    public StateEnum getCurrentState() {
        return currentState;
    }

    public void setCurrentState(StateEnum currentState) {
        this.currentState = currentState;
    }

    public User getRoundWinner() {
        return roundWinner;
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

    /**
     * Select a player who is on turn next to the current one.
     *
     * @return <tt>true</tt> if there are still players to continue, <tt>false</tt> if the last player has played
     */
    public boolean nextOnTurn() {
        onTurnIndex++;
        if (onTurnIndex >= players.size()) {
            onTurnIndex = 0;
            return false;
        }
        return true;
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
        currentState = StateEnum.AWAITING_START_OF_ROUND;
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
        currentState = StateEnum.PROPHECY_TIME;
        playedCards.clear();
        prepareDeck();

        // take cards from the deck for each player
        for (Map.Entry<User, List<Card>> entry : cards.entrySet()) {
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
    }
}
