package board.games.wizard;

import board.games.Games;
import board.room.Room;
import board.user.User;
import board.utils.Json;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class WizardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Room room = (Room) req.getSession().getAttribute("room");
        User user = (User) req.getSession().getAttribute("user");

        // cut the wizard prefix from the path
        String path = req.getRequestURI().substring(Games.WIZARD.gamePath().length());

        WizardState state = (WizardState) room.parameters().get("state");
        if (state == null) {
            // initialize the game
            state = new WizardState(room);
            room.parameters().put("state", state);
        }

        switch (path) {
            case "/get-state":
                renderState(resp, user, state);
                break;
            case "/get-message":
                renderMessage(resp, user, state);
                break;
            case "/play":
                playCard(req, resp, user, state);
                break;
            default:
                renderHtml(req, resp, user, room, state);
                break;
        }
    }

    private void renderState(HttpServletResponse resp, User user, WizardState state) throws IOException {
        Json.renderJson(resp, new GetStateJson(
                user.getId(),
                user.getName(),
                state.getOnTurn().getId(),
                state.getCardsInHand().get(user.getId()),
                state.getActualRoundCards(),
                state.getTrump()
        ));
    }

    /**
     * This method is called by players who are not on turn. We need to inform them what has happened in between
     * of get-message requests.
     */
    private void renderMessage(HttpServletResponse resp, User user, WizardState state) throws IOException {
        Json.renderJson(resp, new GetMessageJson(
                state.getOnTurn().getId(),
                state.getActualRoundCards()
        ));
    }

    private void playCard(HttpServletRequest req, HttpServletResponse resp, User user, WizardState state) throws IOException {
        // todo: get the move sorted

        renderMessage(resp, user, state);
    }

    private void renderHtml(HttpServletRequest req, HttpServletResponse resp, User user, Room room, WizardState state) throws ServletException, IOException {
        // a scaffold to create card slots for each player
        List<Integer> slots = new ArrayList<>(20);
        for (int i = 0; i < 20; i++) slots.add(i);

        req.setAttribute("players", sortPlayers(user, room));
        req.setAttribute("slots", slots);
        req.setAttribute("cards", Card.allCards());
        getServletContext().getRequestDispatcher("/th/wizard/wizard.th").forward(req, resp);
    }

    /**
     * Sort players in such a way that the actual user is always the first one.
     *
     * @param user a user from the session (actual user)
     * @param room the room contains a list of players
     * @return players sorted that the actual user is always the first one but neighbors are still the same
     * for each player
     */
    private Object sortPlayers(User user, Room room) {
        Deque<User> players = new LinkedList<>(room.players());

        while (!players.peek().getId().equals(user.getId())) {
            // take the first player and put it to the end
            User player = players.poll();
            players.addLast(player);
        }

        return players;
    }
}
