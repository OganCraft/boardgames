package board.games.chocowitch;

import board.room.Room;
import board.user.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

class GameState {
    private User onTurn;
    private List<User> playersList;
    private Map<String, Map<String, AtomicInteger>> playerCards;
    private List<String> deck;
    private List<String> pulledCards;
    private int intOnTurn;


    public GameState(Room room) {
        List<String> help = new ArrayList<>();
        help.add("butter");
        help.add("cacao");
        help.add("milk");
        help.add("nuts");
        help.add("sugar");
        help.add("vanilla");

        playerCards = new LinkedHashMap<>();
        pulledCards = new ArrayList<>();
        playersList = new ArrayList<>(room.players());

        intOnTurn = 0;
        onTurn = playersList.get(intOnTurn);


        for (User player : room.players()) {
            Map<String, AtomicInteger> ingredients = new LinkedHashMap<>();

            for(String ingredient : help) {
                ingredients.put(ingredient, new AtomicInteger());
            }

            playerCards.put(player.getName(), ingredients);
        }
        deck = CardDeckMake.deckMake();
    }

    public Map<String, Map<String, AtomicInteger>> getPlayerCards() {
        return playerCards;
    }

    public List<String> getDeck() {
        return deck;
    }

    public List<String> getPulledCards() {
        return pulledCards;
    }

    public User getOnTurn() {
        return onTurn;
    }

    public List<User> getPlayersList() {
        return playersList;
    }

    public int getIntOnTurn() {
        return intOnTurn;
    }

    public void setIntOnTurn(int intOnTurn) {
        this.intOnTurn = intOnTurn;
    }

    public void setPulledCards(List<String> pulledCards) {
        this.pulledCards = pulledCards;
    }

    public void setPlayerCards(Map<String, Map<String, AtomicInteger>> playerCards) {
        this.playerCards = playerCards;
    }
}
