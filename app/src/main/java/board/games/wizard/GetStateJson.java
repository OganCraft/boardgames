package board.games.wizard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class representing all information required during /get-state call.
 */
class GetStateJson {
    private String userId;
    private String userName;
    private String onTurn;
    private Card trump;

    private List<Card> myCards;
    private List<ActualCards> actualCards;

    public GetStateJson(String userId, String userName, String onTurn, List<Card> myCards, Map<String, Card> actualCards, Card trump) {
        this.userId = userId;
        this.userName = userName;
        this.onTurn = onTurn;
        this.myCards = myCards;
        this.trump = trump;

        this.actualCards = toActualCards(actualCards);
    }

    static List<ActualCards> toActualCards(Map<String, Card> actualCards) {
        List<ActualCards> list = new ArrayList<>(actualCards.size());
        for (Map.Entry<String, Card> entry : actualCards.entrySet())
            list.add(new ActualCards(entry.getKey(), entry.getValue()));
        return list;
    }

    static class ActualCards {
        private String userId;
        private Card card;

        public ActualCards(String userId, Card card) {
            this.userId = userId;
            this.card = card;
        }
    }
}
