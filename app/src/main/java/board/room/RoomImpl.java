package board.room;

import board.games.Games;
import board.user.User;

import java.util.*;

public class RoomImpl implements Room {
    private Games game;
    private User owner;
    private Set<User> players;
    private boolean started;
    private boolean closed;
    private Map<String, Object> parameters;
    private boolean gameOver;

    public RoomImpl(Games game, User owner) {
        this.game = game;
        this.owner = owner;
        this.started = false;
        this.closed = false;
        this.gameOver = false;
        this.players = new LinkedHashSet<>();
        this.players.add(owner);
        this.parameters = new HashMap<>();
    }

    @Override
    public String id() {
        return game.getName() + ":" + owner.getName();
    }

    @Override
    public Games game() {
        return game;
    }

    @Override
    public Collection<User> players() {
        return players;
    }

    @Override
    public User owner() {
        return owner;
    }

    @Override
    public void join(User user) {
        players.add(user);
    }

    @Override
    public void leave(User user) {
        players.remove(user);
    }

    @Override
    public void start() {
        started = true;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean isGameOver() {
        return gameOver;
    }

    @Override
    public void gameOver() {
        gameOver = true;
    }

    @Override
    public void close() {
        closed = true;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public Map<String, Object> parameters() {
        return parameters;
    }

    @Override
    public int hashCode() {
        return id().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RoomImpl)
            return id().equals(((RoomImpl) obj).id());
        else
            return false;
    }
}
