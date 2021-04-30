package board.games.wizard;

import board.user.User;

import java.util.Map;

public class NewRoundEventBuilder implements EventBuilder {
    @Override
    public Map<String, Object> build(User user, WizardState state) {
        var event = EventBuilder.createEvent("new-round");
        event.put("onTurn", state.getOnTurn().getId());
        event.put("players", state.getPlayers());
        event.put("myCards", state.getCardsInHand().get(user.getId()));
        event.put("trump", state.getTrump());
        event.put("guessTime", state.isGuessTime());
        return event;
    }
}