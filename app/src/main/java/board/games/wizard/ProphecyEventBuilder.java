package board.games.wizard;

import board.user.User;

import java.util.Map;

public class ProphecyEventBuilder implements EventBuilder{
    /**
     * User id of the player who did the prophecy.
     */
    private String userId;
    /**
     * The prophecy value.
     */
    private int prophecy;
    /**
     * Every prophecy has been made.
     */
    private boolean allReady;

    public ProphecyEventBuilder(String userId, int prophecy, boolean allReady) {
        this.userId = userId;
        this.prophecy = prophecy;
        this.allReady = allReady;
    }

    @Override
    public Map<String, Object> build(User user, WizardState state) {
        var event = EventBuilder.createEvent("prophecy");
        event.put("userId", userId);
        event.put("prophecy", prophecy);
        event.put("allReady", allReady);
        event.put("onTurn", state.getOnTurn().getId());
        return event;
    }
}
