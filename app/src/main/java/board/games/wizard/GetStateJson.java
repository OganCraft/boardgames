package board.games.wizard;

import board.user.User;

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

    private List<User> players; // order of players in this round
    private List<Card> myCards;
    private List<PlayedCard> playedCards;

    public GetStateJson(String userId, String userName, String onTurn, List<User> players, List<Card> myCards, Map<String, Card> playedCards, Card trump) {
        this.userId = userId;
        this.userName = userName;
        this.onTurn = onTurn;
        this.players = players;
        this.myCards = myCards;
        this.trump = trump;

        this.playedCards = toPlayedCards(playedCards);
    }

    static List<PlayedCard> toPlayedCards(Map<String, Card> actualCards) {
        List<PlayedCard> list = new ArrayList<>(actualCards.size());
        for (Map.Entry<String, Card> entry : actualCards.entrySet())
            list.add(new PlayedCard(entry.getKey(), entry.getValue()));
        return list;
    }

    static class PlayedCard {
        private String userId;
        private Card card;

        public PlayedCard(String userId, Card card) {
            this.userId = userId;
            this.card = card;
        }
    }
}
