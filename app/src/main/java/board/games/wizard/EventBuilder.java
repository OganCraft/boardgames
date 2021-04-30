package board.games.wizard;

import board.user.User;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The builder allows creation of a user specific event.
 */
public interface EventBuilder {
    /**
     * Build an event from the user's perspective.
     * @return event object (to be serialized as JSON)
     */
    Map<String, Object> build(User user, WizardState state);

    static Map<String, Object> createEvent(String eventName) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("event", eventName);
            return map;
    }

    static Map<String, Object> errorMessage(String message) {
        var map = createEvent("error");
        map.put("message", message);
        return map;
    }
}
