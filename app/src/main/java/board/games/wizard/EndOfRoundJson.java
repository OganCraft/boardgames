package board.games.wizard;

import board.user.User;

import java.util.List;
import java.util.Map;

class EndOfRoundJson extends EventJson {
    private User winner;
    private List<GetStateJson.ActualCards> actualCards;

    public EndOfRoundJson(Map<String, Card> actualCards) {
        super("end-of-round");
        this.actualCards = GetStateJson.toActualCards(actualCards);
    }
}
