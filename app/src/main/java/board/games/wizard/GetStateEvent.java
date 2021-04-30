package board.games.wizard;

import board.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class representing all information required during /get-state call.
 */
class GetStateEvent {
    private String userId;
    private String userName;

    private Map<String, Object> newRound, playedCards, endOfRound;

    public GetStateEvent(User user, WizardState state) {
        this.userId = user.getId();
        this.userName = user.getName();

        this.newRound = new NewRoundEventBuilder().build(user, state);

        if (state.getRoundWinner() == null)
            this.playedCards = new CardPlayedEventBuilder().build(user, state);
        else
            this.endOfRound = new EndOfRoundEventBuilder().build(user, state);
    }

    private static void removeEventName(Map<String, Object> event) {
        if (event != null)
            event.remove("event");
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
