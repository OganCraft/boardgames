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

    private NewRoundJson newRound;
    private CardPlayedJson playedCards;
    private EndOfRoundJson endOfRound;

    public GetStateJson(User user, WizardState state) {
        this.userId = user.getId();
        this.userName = user.getName();

        this.newRound = new NewRoundJson(user, state);

        this.playedCards = null;
        this.endOfRound = null;

        if (state.getRoundWinner() == null)
            this.playedCards = new CardPlayedJson(state);
        else
            this.endOfRound = new EndOfRoundJson(state.getRoundWinner(), state.getPlayedCards());
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
