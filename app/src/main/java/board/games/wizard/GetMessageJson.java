package board.games.wizard;

import java.util.List;
import java.util.Map;

/**
 * Information for a waiting player for its turn.
 */
public class GetMessageJson {
    private String onTurn;
    private List<GetStateJson.ActualCards> actualCards;

    public GetMessageJson(String onTurn, Map<String, Card> actualCards) {
        this.onTurn = onTurn;
        this.actualCards = GetStateJson.toActualCards(actualCards);
    }
}
