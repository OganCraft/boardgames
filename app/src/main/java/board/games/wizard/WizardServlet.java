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
            case "/play-card":
                playCard(req, resp, user, state);
                break;
            case "/new-round":
                newRound(req, resp, user, state);
                break;
            default:
                renderHtml(req, resp, user, room, state);
                break;
        }
    }

    private void renderState(HttpServletResponse resp, User user, WizardState state) throws IOException {
        Json.renderJson(resp, new GetStateJson(user, state));
    }

    private void renderMessage(HttpServletResponse resp, User user, WizardState state) throws IOException {
        if (state.isEndOfGame())
            renderEndOfGameMessage(resp, user, state);
        else if (state.isEndOfRound())
            renderEndOfRoundMessage(resp, user, state);
        else
            renderCardPlayedMessage(resp, user, state);
    }

    /**
     * This method is called by players who are not on turn. We need to inform them what has happened in between
     * of get-message requests.
     */
    private void renderCardPlayedMessage(HttpServletResponse resp, User user, WizardState state) throws IOException {
        Json.renderJson(resp, new CardPlayedJson(state));
    }

    private void renderEndOfRoundMessage(HttpServletResponse resp, User user, WizardState state) throws IOException {
        Json.renderJson(resp, new EndOfRoundJson(
                state.getRoundWinner(),
                state.getPlayedCards()
        ));
    }

    private void renderEndOfGameMessage(HttpServletResponse resp, User user, WizardState state) {
        // todo
    }

    private void renderErrorMessage(HttpServletResponse resp, String error) throws IOException {
        Json.renderJson(resp, new ErrorJson(error));
    }

    private void playCard(HttpServletRequest req, HttpServletResponse resp, User user, WizardState state) throws IOException {
        Card card = new Card(
                Integer.parseInt(req.getParameter("value")),
                Card.Color.valueOf(req.getParameter("color"))
        );

        String validationMessage = WizardRules.playCard(user, state, card);

        if (validationMessage == null)
            renderMessage(resp, user, state);
        else
            renderErrorMessage(resp, validationMessage);
    }

    private void newRound(HttpServletRequest req, HttpServletResponse resp, User user, WizardState state) throws IOException {
        state.prepareNextRound();
        Json.renderJson(resp, new NewRoundJson(user, state));
    }

    private void renderHtml(HttpServletRequest req, HttpServletResponse resp, User user, Room room, WizardState state) throws ServletException, IOException {
        // a scaffold to create card slots for each player
        List<Integer> slots = new ArrayList<>(20);
        for (int i = 0; i < 20; i++) slots.add(i);

        req.setAttribute("players", room.players());
        req.setAttribute("slots", slots);
        req.setAttribute("cards", Card.allCards());
        getServletContext().getRequestDispatcher("/th/wizard/wizard.th").forward(req, resp);
    }
}
