package board.room;

import board.games.Games;
import board.user.User;

import java.util.ArrayList;
import java.util.List;

public class RoomManagerImpl implements RoomManager {
    private final List<Room> availableRooms;
    private final List<Room> activeRooms;

    public RoomManagerImpl(Games... games) {
        this.availableRooms = new ArrayList<>();
        this.activeRooms = new ArrayList<>();
    }

    @Override
    public List<Room> availableRooms() {
        return availableRooms;
    }

    @Override
    public List<Room> activeRooms() {
        return activeRooms;
    }

    @Override
    public Room createRoom(User owner, Games game) {
        Room room = new RoomImpl(game, owner);
        availableRooms.add(room);
        return room;
    }

    @Override
    public void closeRoom(Room room) {
        availableRooms.remove(room);
        room.close();
        // todo: how to notify all users joined to the room?
        // todo: the room is in their session...
    }

    @Override
    public void startGame(Room room) {
        room.start();
        activeRooms.add(room);
        availableRooms.remove(room);
    }

    @Override
    public void gameOver(Room room) {
        availableRooms.add(room);
        activeRooms.remove(room);
    }
}
