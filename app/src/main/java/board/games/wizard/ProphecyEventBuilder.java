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
     * If everyone told his/her prophecy.
     */
    private boolean allDone;

    public ProphecyEventBuilder(User user, int prophecy, boolean allDone) {
        this.userId = user.getId();
        this.prophecy = prophecy;
        this.allDone = allDone;
    }

    @Override
    public Map<String, Object> build(User user, WizardState state) {
        var event = EventBuilder.createEvent("prophecy");
        event.put("userId", userId);
        event.put("prophecy", prophecy);
        event.put("allDone", allDone);
        event.put("onTurn", state.getOnTurn().getId());
        return event;
    }
}
