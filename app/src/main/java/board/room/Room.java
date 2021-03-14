package board.room;

import board.games.Games;
import board.user.User;

import java.util.Collection;
import java.util.Map;

public interface Room {
    /**
     * Unique identificator of the room.
     *
     * @return the id
     */
    String id();

    /**
     * A game associated with the room.
     *
     * @return the game
     */
    Games game();

    /**
     * Players connected to the game.
     *
     * @return the players
     */
    Collection<User> players();

    /**
     * An owner of the room. The owner is also returned by {@link #players()}.
     *
     * @return a room creator
     */
    User owner();

    /**
     * A player has joined the room.
     *
     * @param user joining user
     */
    void join(User user);

    /**
     * A player has left.
     *
     * @param user leaving user
     */
    void leave(User user);

    /**
     * Begin the game.
     */
    void start();

    /**
     * The owner has started the game.
     *
     * @return <tt>true</tt> if the game is started.
     */
    boolean isStarted();

    /**
     * The game has ended.
     *
     * @return <tt>true</tt> if the game has ended
     */
    boolean isGameOver();

    /**
     * Set game over.
     */
    void gameOver();

    /**
     * Close the room.
     * This operation should be available only for the room owner.
     */
    void close();

    /**
     * The owner has closed the room. It's not available any more.
     *
     * @return <tt>true</tt> if closed, unavailable
     */
    boolean isClosed();

    /**
     * Shared game parameters.
     *
     * @return map of game objects.
     */
    Map<String, Object> parameters();
}
