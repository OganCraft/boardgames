package board.games.chocowitch;

import board.room.Room;
import board.user.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ChocoWitchServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Room room = (Room) req.getSession().getAttribute("room");
        User user = (User) req.getSession().getAttribute("user");
        String path = req.getRequestURI();
        System.out.println(path);
        if ("/chocolate/start".equals(path)) {
            GameState state = new GameState(room);
            room.parameters().put("state", state);
            resp.sendRedirect("/chocolate");
            return;
        }

        ArrayList<String> ingredients = new ArrayList<String>();
        ingredients.add("butter");
        ingredients.add("cacao");
        ingredients.add("milk");
        ingredients.add("nuts");
        ingredients.add("sugar");
        ingredients.add("vanilla");

        GameState state = (GameState) room.parameters().get("state");
        System.out.println(state.getPlayerCards());
        if ("/chocolate/pullCard".equals(path)) {
            cardPull(state);
            pulledCards(state);
            if(!isWitch) {
                state.getPlayerCards().get(user.getName()).get(pulledCard).incrementAndGet();
            }else {
                resp.sendRedirect("/chocolate/endTurnForce");
            }
            return;
        } else if ("/chocolate/endTurn".equals(path)) {
            if(state.getIntOnTurn() + 1 == state.getPlayersList().size()) {
                state.setIntOnTurn(0);
            }else {
                state.setIntOnTurn(state.getIntOnTurn() + 1);
            }
            Map<String, Map<String, AtomicInteger>> playerCards = state.getPlayerCards();
            Map<String, AtomicInteger> myCards = playerCards.get(user.getName());
            List<String> pulledCards = state.getPulledCards();
            for(int i = 0; i < state.getPulledCards().size(); i++) {
                myCards.get(pulledCards.get(i)).incrementAndGet();
            }
            pulledCards.clear();
            return;
        } else if ("/chocolate/endTurnForce".equals(path)) {
            if(state.getIntOnTurn() + 1 == state.getPlayersList().size()) {
                state.setIntOnTurn(0);
            }else {
                state.setIntOnTurn(state.getIntOnTurn() + 1);
            }
            return;
        }
        htmlCode(req, resp);
    }

    String pulledCard;
    boolean isWitch;
    boolean isCardPulled;


    public void cardPull(GameState state) {
        List<String> deck = state.getDeck();
        pulledCard = deck.get(0);
        if (pulledCard.contains("witch")) {
            deck.remove(0);
            isWitch = true;
            isCardPulled = true;
        } else {
            deck.remove(0);
            isWitch = false;
            isCardPulled = true;
        }
    }

    public void pulledCards(GameState state) {
        state.getPulledCards().add(pulledCard);
    }

    private void htmlCode(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Room room = (Room) req.getSession().getAttribute("room");
        User user = (User) req.getSession().getAttribute("user");
        GameState state = (GameState) room.parameters().get("state");
        List<User> players = state.getPlayersList();
        Map<String, AtomicInteger> myCards = state.getPlayerCards().get(user.getName());
        boolean isMyTurn = user == state.getOnTurn();
        req.setAttribute("pulledCards", state.getPulledCards());
        req.setAttribute("pulledCard", pulledCard);
        req.setAttribute("isWitch", isWitch);
        req.setAttribute("myCards", myCards);
        req.setAttribute("players", players);
        req.setAttribute("isMyTurn", isMyTurn);
        getServletContext().getRequestDispatcher("/th/chocowitch/chocolate.th").forward(req, resp);
    }
}
