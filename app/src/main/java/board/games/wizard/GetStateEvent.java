package board.games.wizard;

import board.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class representing all information required during /get-state call.
 */
class GetStateEvent {
    private String event = "get-state";
    private StateEnum currentState;
    private List<User> players;
    private String userId;
    private String userName;
    private boolean prophecyTime;

    private Map<String, Object> newRound, playedCards, endOfRound;
    private List<Map<String, Object>> prophecy;

    public GetStateEvent(User user, WizardState state) {
        this.currentState = state.getCurrentState();
        this.userId = user.getId();
        this.userName = user.getName();

        this.newRound = new NewRoundEventBuilder(true).build(user, state);

        if (state.getCurrentState().equals(StateEnum.END_OF_GAME))
            this.endOfRound = new EndOfRoundEventBuilder().build(user, state);
        else
            this.playedCards = new CardPlayedEventBuilder().build(user, state);

        removeEventName(newRound);
        removeEventName(playedCards);
        removeEventName(endOfRound);

        prophecyTime = false;

        if (currentState.equals(StateEnum.PROPHECY_TIME) || currentState.equals(StateEnum.AWAITING_START_OF_ROUND)) {
            prophecyTime = true;
            prophecy = new ArrayList<>();
            boolean allDone = currentState.equals(StateEnum.AWAITING_START_OF_ROUND);
            int round = state.getRound();
            // send prophecy of each player
            for (User player: state.getPlayers()) {
                List<Score> score = state.getScore().get(player);
                if (score.size() >= round) {
                    // there is a user's prophecy if there are more prophecies than rounds played
                    prophecy.add(
                            new ProphecyEventBuilder(player, score.get(round - 1)
                                    .getProphecy(), allDone).build(user, state)
                    );
                }
            }
        }
    }

    private static void removeEventName(Map<String, Object> event) {
        if (event != null)
            event.remove("event");
    }

    static List<PlayedCard> toPlayedCards(Map<User, Card> actualCards) {
        List<PlayedCard> list = new ArrayList<>(actualCards.size());
        for (Map.Entry<User, Card> entry : actualCards.entrySet())
            list.add(new PlayedCard(entry.getKey().getId(), entry.getValue()));
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
