package board.room;

import board.games.Games;
import board.user.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;

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
        else if ("/room/attach".equals(path))
            roomAttach(req, resp);
        else if ("/room/leave".equals(path))
            roomLeave(req, resp);
        else if ("/room/wait".equals(path))
            roomWait(req, resp);
        else if ("/room/start".equals(path))
            roomStart(req, resp);
        else if ("/room/end".equals(path))
            roomEnd(req, resp);
    }

    /**
     * Print list of available games, list of rooms to join and list of rooms with active games.
     *
     * @param req  http request
     * @param resp http response
     */
    private void roomIndex(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        RoomManager roomManager = (RoomManager) req.getServletContext().getAttribute("roomManager");

        req.setAttribute("games", Games.values());
        req.setAttribute("roomManager", roomManager);
        // todo: print, but disable to join if a user has already joined a room
        // todo: print, but disable to attach if a user is not a member of the active game
        getServletContext().getRequestDispatcher("/th/room/room_index.th").forward(req, resp);
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

    private void roomAttach(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RoomManager roomManager = (RoomManager) req.getServletContext().getAttribute(RoomManager.roomManagerAttribute);
        User user = (User) req.getSession().getAttribute("user");

        String roomId = req.getParameter("room");

        for (Room room : roomManager.activeRooms()) {
            if (room.id().equals(roomId)) {
                if (room.players().contains(user)) {
                    req.getSession().setAttribute("room", room);
                    resp.sendRedirect(room.game().gamePath());
                }
                break;
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

    private void roomWait(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
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

        req.setAttribute("room", room);
        getServletContext().getRequestDispatcher("/th/room/room_wait.th").forward(req, resp);
    }

    private void roomStart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RoomManager roomManager = (RoomManager) req.getServletContext().getAttribute(RoomManager.roomManagerAttribute);
        Room room = (Room) req.getSession().getAttribute("room");

        if (room.isStarted()) {
            resp.sendRedirect(room.game().gamePath());
        } else {
            roomManager.startGame(room);
            // the convention: /game_path/start should initialize the game
            resp.sendRedirect(room.game().gamePath() + "/start");
        }
    }

    private void roomEnd(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RoomManager roomManager = (RoomManager) req.getServletContext().getAttribute(RoomManager.roomManagerAttribute);
        Room room = (Room) req.getSession().getAttribute("room");

        roomManager.closeRoom(room);
        req.getSession().removeAttribute("room");
        resp.sendRedirect("/room");
    }
}
