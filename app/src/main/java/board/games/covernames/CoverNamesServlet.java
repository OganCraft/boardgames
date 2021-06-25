package board.games.covernames;

import board.room.Room;
import board.user.User;
import board.utils.Json;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CoverNamesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Room room = (Room) req.getSession().getAttribute("room");
        User user = (User) req.getSession().getAttribute("user");
        String path = req.getRequestURI();

        GameState state = (GameState) room.parameters().get("state");
        if (state == null) {
            state = new GameState(room);
            room.parameters().put("state", state);
        }
        System.out.println(state.getCards());
/*        if (state.getBlueTeam().size() == 0) {
            resp.sendRedirect("/names/choose");
        }
        // path /names/choose/random - randomly choose
        // path /names/choose/pchoose - players choose
        if ("/names/random".equals(path)) {
            List<User> playersList = new ArrayList<>(room.players());
            Collections.shuffle(playersList);
            for (int i = 0; i < playersList.size(); i++) {
                if (i % 2 == 0) {
                    state.getBlueTeam().add(playersList.get(i));
                }else {
                    state.getRedTeam().add(playersList.get(i));
                }
            }
            resp.sendRedirect("/names/play");
        }else if ("/names/choose/pchoose/*".equals(path) || "/names/choose/pchoose".equals(path)) {
            if ("/names/choose/pchoose".equals(path)) {
                if (state.getBlueTeam().contains(user)) {
                    state.getBlueTeam().remove(user);
                }else if (state.getRedTeam().contains(user)) {
                    state.getRedTeam().remove(user);
                }
                // button so join teams blue redirect to waitBlue, red to waitRed
            }else if ("/names/choose/pchoose/waitblue".equals(path)) {
                state.getBlueTeam().add(user);
            }else if ("/names/choose/pchoose/waitred".equals(path)) {
                state.getRedTeam().add(user);
            }
        } */

        if ("/names/get-state".equals(path)) {
            NamesGetState event = new NamesGetState(user, state);
            Json.renderJson(resp, event);
        } else if ("/names/choose-team".equals(path)) {

        } else {
            htmlCode(req, resp, state);
        }
    }

    private void htmlCode(HttpServletRequest req, HttpServletResponse resp, GameState state) throws ServletException, IOException {
        Room room = (Room) req.getSession().getAttribute("room");
        User user = (User) req.getSession().getAttribute("user");
        String team;
        if (state.getRedTeam().contains(user)) {
            team = "červený";
        } else {
            team = "modrý";
        }
        boolean amIHint = state.getHintGuys().contains(user);

        req.setAttribute("team", team);
        req.setAttribute("cards", state.getCards());
        req.setAttribute("hint", state.getHint());
        req.setAttribute("amIHint", amIHint);
        req.setAttribute("redTeamPlayers", state.getRedTeam());
        req.setAttribute("blueTeamPlayers", state.getBlueTeam());
        getServletContext().getRequestDispatcher("/th/covernames/names.th").forward(req, resp);
    }
}
