package board.room;

import board.games.Games;
import board.user.User;

import java.util.List;

public interface RoomManager {
    public static final String roomManagerAttribute = "roomManager";

    /**
     * Rooms which ara available for players to connect to.
     *
     * @return opened rooms
     */
    List<Room> availableRooms();

    /**
     * Rooms with an active games. The games are not available for new players.
     *
     * @return active rooms
     */
    List<Room> activeRooms();

    /**
     * Create new room. The room will start to be available in {@link #availableRooms()}.
     *
     * @param owner a user who created the room
     * @param game  a game associated with the room
     * @return a room instance
     */
    Room createRoom(User owner, Games game);

    /**
     * The room is not available any more.
     *
     * @param room the room
     */
    void closeRoom(Room room);

    /**
     * A game begins. No other player can join the game.
     * The room will be moved from opened rooms to active rooms.
     *
     * @param room the room
     */
    void startGame(Room room);

    /**
     * A game associated with the room has ended.
     * The room is moved from active rooms to opened rooms.
     *
     * @param room the room
     */
    void gameOver(Room room);
}
