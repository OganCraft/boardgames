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

/**
 * todo:
 * - end round event
 *      - start the prophecy time
 *      - or end the game completely
 * - optimize GetState request
 *      - chain of individual requests?
 *      - start getState can avoid 1000ms request delay
 *      - end of getState can restore the request delay
 *      - chain of individual events to avoid one fat event which contains
 *        several events - removal of code duplicity
 *      - the chain of events can be generated during one request and
 *        the event queue will get empty through quick requests
 * - web design
 *      - table with players/cards:
 *          - remove the header, for each player create a box with two cells
 *              - name, wins in format current/prophesied, hoovering the
 *                values may give detailed description of each number
 *              - the card
 *              - wins number can be colored, < prophesied: black,
 *                == prophesied: dark green, > prophesied: dark red
 *          - with such boxes the layout can be modified, now is "one player
 *            per row", with the new boxes, players can be inlined to better
 *            utilize the display are (especially computers with wide screens,
 *            mobiles are the opposite)
 *      - score table
 *          - for mobiles keep it as it is (high)
 *          - for wide screens, place it horizontally
 */
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
            events.clearUser(user);  // everything is sent in this state
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
                    case "/end-round":
                        endRound(user, state, events);
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
        LogRequest.request(req, event);
    }

    private void playCard(HttpServletRequest req, User user, WizardState state, EventDeque events) {
        Card card = new Card(
                Integer.parseInt(req.getParameter("value")),
                Card.Color.valueOf(req.getParameter("color"))
        );

        String validationMessage = WizardRules.playCard(user, state, card);

        if (validationMessage == null) {
            if (state.getCurrentState().equals(StateEnum.END_OF_ROUND))
                events.createEvent(state, new EndOfRoundEventBuilder());
            else
                events.createEvent(state, new CardPlayedEventBuilder());
        } else
            throw new WizardException(validationMessage);
    }

    private void newRound(User user, WizardState state, EventDeque events) {
        if (!state.getCurrentState().equals(StateEnum.AWAITING_START_OF_ROUND))
            throw new WizardException("Nelze začít nové kolo, současné ještě neskončilo.");

        state.setCurrentState(StateEnum.GAME);

        int cardsInHand = state.getCardsInHand().get(user).size();
        if (cardsInHand > 0) {
            state.prepareRoundShort();
            events.createEvent(state, new NewRoundEventBuilder(false));
        } else {
            state.prepareRoundLong();
            events.createEvent(state, new NewRoundEventBuilder(true));
        }
    }

    private void endRound(User user, WizardState state, EventDeque events) {
        if (!state.getCurrentState().equals(StateEnum.GAME))
            throw new WizardException("Nelze ukončit kolo, momentálně se nehraje.");

        // todo: EndRoundEvent
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

        boolean allDone = !state.nextOnTurn();    // new player on turn, will be used in the following event
        events.createEvent(state, new ProphecyEventBuilder(user, prophecy, allDone));
        if (allDone)
            state.setCurrentState(StateEnum.AWAITING_START_OF_ROUND);
    }

    private void renderHtml(HttpServletRequest req, HttpServletResponse resp, User user, Room room, WizardState state) throws ServletException, IOException {
        // a scaffold to create card slots for each player
        int number = 60 / room.players().size();
        List<Integer> slots = new ArrayList<>(number);
        for (int i = 0; i < number; i++) slots.add(i);

        req.setAttribute("players", room.players());
        req.setAttribute("slots", slots);
        req.setAttribute("cards", Card.allCards());
        getServletContext().getRequestDispatcher("/th/wizard/wizard.th").forward(req, resp);
    }
}
