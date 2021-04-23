package board.games.wizard;

import java.util.List;
import java.util.Map;

/**
 * Information for a waiting player for its turn.
 */
public class CardPlayedJson extends EventJson {
    private String onTurn;
    private List<GetStateJson.PlayedCard> actualCards;

    public CardPlayedJson(String onTurn, Map<String, Card> actualCards) {
        super("card-played");
        this.onTurn = onTurn;
        this.actualCards = GetStateJson.toPlayedCards(actualCards);
    }
}
