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
        if ("/chocolate/start".equals(path)) {
            GameState state = new GameState(room);
            room.parameters().put("state", state);
            resp.sendRedirect("/chocolate");
            return;
        }

        GameState state = (GameState) room.parameters().get("state");
        if ("/chocolate/pullCard".equals(path)) {
            cardPull(state);
            pulledCards(state);
            if (isWitch) {
                resp.sendRedirect("/chocolate/endTurnForce");
            }
        } else if ("/chocolate/endTurn".equals(path)) {
            addCards(state, req);
        } else if ("/chocolate/endTurnForce".equals(path)) {
            if (state.getIntOnTurn() + 1 == state.getPlayersList().size()) {
                state.setIntOnTurn(0);
            } else {
                state.setIntOnTurn(state.getIntOnTurn() + 1);
            }
            state.getPulledCards().clear();
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

    public void addCards(GameState state, HttpServletRequest req) {
        User user = (User) req.getSession().getAttribute("user");
        if (state.getIntOnTurn() + 1 == state.getPlayersList().size()) {
            state.setIntOnTurn(0);
        } else {
            state.setIntOnTurn(state.getIntOnTurn() + 1);
        }
        Map<String, Map<String, AtomicInteger>> playerCards = state.getPlayerCards();
        Map<String, AtomicInteger> myCards = playerCards.get(user.getName());
        List<String> pulledCards = state.getPulledCards();
        for (int i = 0; i < state.getPulledCards().size(); i++) {
            myCards.get(pulledCards.get(i)).incrementAndGet();
        }
        pulledCards.clear();
    }

    public void pulledCards(GameState state) {
        state.getPulledCards().add(pulledCard);
    }

    public void gameOver(HttpServletRequest req, HttpServletResponse resp) {
        Room room = (Room) req.getSession().getAttribute("room");
        User user = (User) req.getSession().getAttribute("user");
        GameState state = (GameState) room.parameters().get("state");
        boolean gameOver = false;
        for (User player: state.getPlayersList()) {
            gameOver = true;
            for (int j = 0; j < 6; j++) {
                String playerName = player.getName();
                String ingredientName = state.getIngredients().get(j);
                AtomicInteger ingredientsCount = state.getPlayerCards().get(playerName).get(ingredientName);
                if (ingredientsCount.intValue() == 0) {
                    gameOver = false;
                    break;
                }
                if (j == 5) {
                    state.setWinner(player.getName());
                    state.setGameOver(true);
                    return;
                }
            }
        }
        state.setGameOver(gameOver);
        if (state.getDeck().size() == 0) {
            if (state.getPulledCards().size() == 0) {
                state.setGameOver(true);
                state.setWinner("nobody");
            } else {
                addCards(state, req);
                gameOver(req, resp);
            }
        }
    }

    private void htmlCode(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Room room = (Room) req.getSession().getAttribute("room");
        User user = (User) req.getSession().getAttribute("user");
        GameState state = (GameState) room.parameters().get("state");
        List<User> players = state.getPlayersList();
        boolean isMyTurn = user == state.getPlayersList().get(state.getIntOnTurn());
        gameOver(req, resp);

        req.setAttribute("pulledCards", state.getPulledCards());
        req.setAttribute("pulledCard", pulledCard);
        req.setAttribute("isWitch", isWitch);
        req.setAttribute("players", players);
        req.setAttribute("isMyTurn", isMyTurn);
        req.setAttribute("gameOver", state.isGameOver());
        req.setAttribute("winner", state.getWinner());
        req.setAttribute("ingredients", state.getIngredients());
        req.setAttribute("playerCards", state.getPlayerCards());
        getServletContext().getRequestDispatcher("/th/chocowitch/chocolate.th").forward(req, resp);
    }
}
