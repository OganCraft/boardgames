package board.games.wizard;

import board.room.Room;
import board.user.User;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Storage of events for each user.
 */
public class EventDeque {
    private Map<User, Deque<Object>> deque;

    public EventDeque(Room room) {
        deque = new HashMap<>();

        for (User player : room.players())
            deque.put(player, new LinkedList<>());
    }

    /**
     * If a user requests the whole game state, all his events can be thrown away.
     *
     * @param user the user whom to clear the event queue
     */
    public void clearUser(User user) {
        deque.get(user).clear();
    }

    /**
     * Take an event (if it does exist) for the given user.
     *
     * @param user a user requesting an event
     * @return an event object
     */
    public Object getEvent(User user) {
        Deque<Object> events = deque.get(user);

        if (events.isEmpty())
            return EventBuilder.createEvent("none");

        return events.removeFirst();
    }

    /**
     * Create the same event for each player.
     *
     * @param state   the game state
     * @param builder an event builder
     */
    public void createEvent(WizardState state, EventBuilder builder) {
        for (Map.Entry<User, Deque<Object>> entry : deque.entrySet())
            entry.getValue().addLast(builder.build(entry.getKey(), state));
    }
}
