package board.games.wizard;

import java.util.List;
import java.util.Map;

/**
 * Class representing all information required during /get-state call.
 */
public class GetStateJson {
    private String userId;
    private String userName;
    private String onTurn;
    private Card trump;

    private List<Card> myCards;
    private Map<String, Card> actualCards;

    public GetStateJson(String userId, String userName, String onTurn, List<Card> myCards, Map<String, Card> actualCards, Card trump) {
        this.userId = userId;
        this.userName = userName;
        this.onTurn = onTurn;
        this.myCards = myCards;
        this.actualCards = actualCards;
        this.trump = trump;
    }
}
