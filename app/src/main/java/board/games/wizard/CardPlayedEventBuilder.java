package board.games.wizard;

import board.user.User;

import java.util.Map;

/**
 * Information for a waiting player for its turn.
 */
public class CardPlayedEventBuilder implements EventBuilder {
    @Override
    public Map<String, Object> build(User user, WizardState state) {
        var map = EventBuilder.createEvent("card-played");
        map.put("onTurn", state.getOnTurn().getId());
        map.put("playedCards", GetStateEvent.toPlayedCards(state.getPlayedCards()));
        return map;
    }
}
