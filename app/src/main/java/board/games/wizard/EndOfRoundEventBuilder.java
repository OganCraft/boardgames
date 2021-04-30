package board.games.wizard;

import board.user.User;

import java.util.Map;

class EndOfRoundEventBuilder implements EventBuilder {
    @Override
    public Map<String, Object> build(User user, WizardState state) {
        var event = EventBuilder.createEvent("end-of-round");
        event.put("winner", state.getRoundWinner());
        event.put("playedCards", GetStateEvent.toPlayedCards(state.getPlayedCards()));
        return event;
    }
}
