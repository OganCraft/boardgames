package board.games.wizard;

import board.room.Room;
import board.user.User;

import java.security.SecureRandom;
import java.util.*;

/**
 * State of the game. Number of players is variable.
 */
class WizardState {
    /**
     * Which round currently is played.
     */
    private int round;
    private Map<String, List<Card>> cards;
    private Map<String, List<Score>> score;
    private Map<String, List<Boolean>> wins;
    private Map<String, Card> actualRoundCards;
    private Queue<Card> deck;
    private Card trump;
    private User onTurn;

    public WizardState(Room room) {
        round = 0;
        cards = new HashMap<>();
        score = new HashMap<>();
        wins = new HashMap<>();
        for (User player : room.players()) {
            cards.put(player.getId(), new ArrayList<>());
            score.put(player.getId(), new ArrayList<>());
        }
        actualRoundCards = new HashMap<>();

        prepareNextRound();

        // random selection of a player who is the first,
        // next time, the winner of a round will be the first to play
        int onTurnRandom = new SecureRandom().nextInt(room.players().size());
        for (User player : room.players()) {
            if (onTurnRandom-- == 0) {
                onTurn = player;
                break;
            }
        }
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

    public User getOnTurn() {
        return onTurn;
    }

    public Card getTrump() {
        return trump;
    }

    private void prepareDeck() {
        LinkedList<Card> c = new LinkedList<>(Card.allCards());
        Collections.shuffle(c, new Random(System.currentTimeMillis()));
        deck = c;
    }

    public void prepareNextRound() {
        round++;
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
    }
}
