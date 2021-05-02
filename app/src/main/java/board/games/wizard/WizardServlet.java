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
        EventDeque events = (EventDeque) room.parameters().get("events");
        if (state == null) {
            // initialize the game
            state = new WizardState(room);
            events = new EventDeque(room);
            room.parameters().put("state", state);
            room.parameters().put("events", events);
        }

        Object event;

        if ("/get-state".equals(path)) {
            events.clearUser(user);  // everything is sent in the state
            event = new GetStateEvent(user, state);
        } else {
            try {
                switch (path) {
                    case "/get-event":
                        break;
                    case "/play-card":
                        playCard(req, user, state, events);
                        break;
                    case "/new-round":
                        newRound(user, state, events);
                        break;
                    case "/prophecy":
                        prophecy(req, user, state, events);
                        break;
                    default:
                        renderHtml(req, resp, user, room, state);
                        return;
                }
                event = events.getEvent(user);
            } catch (WizardException e) {
                event = EventBuilder.errorMessage(e.getMessage());
            } catch (Exception e) {
                e.printStackTrace(System.err);
                throw e;
            }
        }

        Json.renderJson(resp, event);
    }

    private void playCard(HttpServletRequest req, User user, WizardState state, EventDeque events) {
        Card card = new Card(
                Integer.parseInt(req.getParameter("value")),
                Card.Color.valueOf(req.getParameter("color"))
        );

        String validationMessage = WizardRules.playCard(user, state, card);

        if (validationMessage == null) {
            if (state.isEndOfRound())
                events.createEvent(state, new EndOfRoundEventBuilder());
            else
                events.createEvent(state, new CardPlayedEventBuilder());
        } else
            throw new WizardException(validationMessage);
    }

    private void newRound(User user, WizardState state, EventDeque events) {
        if (!state.isEndOfRound())
            throw new WizardException("Nelze začít nové kolo, současné ještě neskončilo.");

        int cardsInHand = state.getCardsInHand().get(user).size();
        if (cardsInHand > 0) {
            state.prepareRoundShort();
            events.createEvent(state, new NewRoundEventBuilder(false));
        } else {
            state.prepareRoundLong();
            events.createEvent(state, new NewRoundEventBuilder(true));
        }
    }

    private void prophecy(HttpServletRequest req, User user, WizardState state, EventDeque events) {
        if (!user.equals(state.getOnTurn()))
            throw new WizardException("Nejsi na tahu!");

        int prophecy;
        try {
            prophecy = Integer.parseInt(req.getParameter("prophecy"));
        } catch (NumberFormatException e) {
            throw new WizardException("Proroctví musí být číslo");
        }

        int maxProphecy = state.getCardsInHand().get(user).size();
        if (prophecy < 0 || prophecy > maxProphecy)
            throw new WizardException("Proroctví musí být číslo od 0 do " + maxProphecy);

        List<Score> score = state.getScore().get(user);
        score.add(new Score(prophecy));

        boolean allReady = !state.nextOnTurn();
        if (allReady)
            state.setProphecyTime(false);
        events.createEvent(state, new ProphecyEventBuilder(user.getId(), prophecy, allReady));
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
