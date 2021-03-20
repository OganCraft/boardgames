package board.room;

import board.games.Games;
import board.user.User;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

public class RoomServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getRequestURI();

        if ("/room".equals(path))
            roomIndex(req, resp);
        else if ("/room/create".equals(path))
            roomCreate(req, resp);
        else if ("/room/join".equals(path))
            roomJoin(req, resp);
        else if ("/room/leave".equals(path))
            roomLeave(req, resp);
        else if ("/room/wait".equals(path))
            roomWait(req, resp);
        else if ("/room/start".equals(path))
            roomStart(req, resp);
        else if ("/room/end".equals(path))
            roomEnd(req, resp);
    }

    private void roomIndex(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RoomManager roomManager = (RoomManager) req.getServletContext().getAttribute("roomManager");

        // print list of rooms and list of available games
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Rooms</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<table border=\"1\">\n" +
                "    <tr><th>Games</th><th>Available Rooms</th><th>Game in progress</th></tr>\n");

        // games
        html.append("<tr><td>");
        for (Games game : Games.values()) {
            html.append("<a href=/room/create?game=").append(game.name()).append(">");
            html.append(game.getName()).append("</a>").append("<br>");
        }
        html.append("\n</td><td>\n");

        for (Room room : roomManager.availableRooms()) {
            html.append("<a href=/room/join?room=").append(room.id()).append(">");
            html.append(room.game().getName()).append("</a> (created by ")
                    .append(room.owner().getName()).append(")<br>");
        }
        html.append("</td><td>");
        for (Room room : roomManager.activeRooms()) {
            html.append("<a href=/room/join?room=").append(room.id()).append(">");
            html.append(room.game().getName()).append("</a> (created by ")
                    .append(room.owner().getName()).append(")<br>");
        }

        html.append("</td></tr>");

        html.append("</table>\n" +
                "</body>\n" +
                "</html>");

        resp.getWriter().println(html);
    }

    private void roomCreate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RoomManager roomManager = (RoomManager) req.getServletContext().getAttribute(RoomManager.roomManagerAttribute);
        User user = (User) req.getSession().getAttribute("user");

        String gameName = req.getParameter("game");
        Games game = Games.valueOf(gameName);
        // todo: check the gameName is correct

        // todo: check the user is not member of any other room
        Room room = roomManager.createRoom(user, game);
        req.getSession().setAttribute("room", room);

        resp.sendRedirect("/room/wait");
    }

    private void roomJoin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RoomManager roomManager = (RoomManager) req.getServletContext().getAttribute(RoomManager.roomManagerAttribute);
        User user = (User) req.getSession().getAttribute("user");

        String roomId = req.getParameter("room");

        for (Room room : roomManager.availableRooms()) {
            if (room.id().equals(roomId)) {
                room.join(user);
                req.getSession().setAttribute("room", room);
                resp.sendRedirect("/room/wait");
                return;
            }
        }

        resp.sendRedirect("/room");
    }

    private void roomLeave(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Room room = (Room) req.getSession().getAttribute("room");
        User user = (User) req.getSession().getAttribute("user");

        room.leave(user);
        resp.sendRedirect("/room");
    }

    private void roomWait(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Room room = (Room) req.getSession().getAttribute("room");

        if (room.isClosed()) {
            req.getSession().removeAttribute("room");
            resp.sendRedirect("/room");
            return;
        }

        if (room.isStarted()) {
            resp.sendRedirect(room.game().gamePath());
            return;
        }

        User user = (User) req.getSession().getAttribute("user");

        StringBuilder html = new StringBuilder();
        html.append("<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Waiting room</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "Room owner: ").append(room.owner().getName()).append("<br>\n" +
                "\n");

        html.append("Room capacity:\n" +
                "<ul>\n" +
                "    <li>min: ").append(room.game().minPlayers()).append("</li>\n" +
                "    <li>max: ").append(room.game().maxPlayers()).append("</li>\n" +
                "    <li>current: ").append(room.players().size()).append("</li>\n" +
                "</ul>\n" +
                "Players:\n" +
                "<ul>\n");


        for (User player : room.players()) {
            html.append("    <li>").append(player.getName()).append("</li>\n");
        }

        html.append("</ul>\n" +
                "\n" +
                "<a href=\"/room/wait\">reload</a><br>\n");

        if (user == room.owner()) {
            if (room.game().minPlayers() <= room.players().size())
                html.append("<a href=\"/room/start\">start</a><br>\n");
            html.append("<a href=\"/room/end\">end</a><br>\n");
        } else {
            html.append("<a href=\"/room/leave\">leave</a><br>\n");
        }

        html.append("\n" +
                "</body>\n" +
                "</html>");
        resp.getWriter().println(html);
    }

    private void roomStart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RoomManager roomManager = (RoomManager) req.getServletContext().getAttribute(RoomManager.roomManagerAttribute);
        Room room = (Room) req.getSession().getAttribute("room");
        roomManager.startGame(room);

        // the convention: /game_path/start should initialize the game
        resp.sendRedirect(room.game().gamePath() + "/start");
    }

    private void roomEnd(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RoomManager roomManager = (RoomManager) req.getServletContext().getAttribute(RoomManager.roomManagerAttribute);
        Room room = (Room) req.getSession().getAttribute("room");

        roomManager.closeRoom(room);
        req.getSession().removeAttribute("room");
        resp.sendRedirect("/room");
    }
}
