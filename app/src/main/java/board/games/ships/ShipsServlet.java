package board.games.ships;

import board.room.Room;
import board.user.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

import static board.games.utils.Rng.rng;

/**
 * todo: communication between users
 */
public class ShipsServlet extends HttpServlet {

    private static final class Boards {
        int[][] shipBoard, enemyBoard;

        public Boards(int[][] shipBoard, int[][] enemyBoard) {
            this.shipBoard = shipBoard;
            this.enemyBoard = enemyBoard;
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        Room room = (Room) request.getSession().getAttribute("room");
        User user = (User) request.getSession().getAttribute("user");

        String path = request.getRequestURI();
        if ("/ships/start".equals(path)) {
            initGame(room);
        }

        Boards boards = getBoards(room, user);
        htmlCode(boards, resp, Collections.emptyList(), false);
    }

    private void initGame(Room room) {
        for (User player : room.players()) {
            room.parameters().put(player.getName(), boardGenerate());
        }
    }

    private Boards getBoards(Room room, User user) {
        // create copy of players, so we can freely modify the collection
        List<String> players = new ArrayList<>(room.parameters().keySet());

        int[][] shipBoard = (int[][]) room.parameters().get(user.getName());
        players.remove(user.getName());
        // the name which left is the enemy
        int[][] enemyBoard = (int[][]) room.parameters().get(players.get(0));

        return new Boards(shipBoard, enemyBoard);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Room room = (Room) req.getSession().getAttribute("room");
        User user = (User) req.getSession().getAttribute("user");

        Boards boards = getBoards(room, user);

        List<String> messages = new ArrayList<>();
        String fire = req.getParameter("fire");
        int[] fireLoc = fire(fire, messages);

        int[][] shipBoard = boards.shipBoard;
        int[][] enemyBoard = boards.enemyBoard;

        boolean gameOver = false;
        if (fireLoc != null) {
            if (enemyBoard[fireLoc[0]][fireLoc[1]] == 0) {
                enemyBoard[fireLoc[0]][fireLoc[1]] = 2;
                messages.add("Aw, you missed.");
                enemyShoot(shipBoard, messages);
            } else if (enemyBoard[fireLoc[0]][fireLoc[1]] == 1) {
                enemyBoard[fireLoc[0]][fireLoc[1]] = 3;
                if (sunkenShip(enemyBoard, fireLoc)) {
                    messages.add("Captain, you are the best. Their ship is sinking.");
                    waterAround(enemyBoard, fireLoc);
                    if (gameOver(enemyBoard)) {
                        gameOver = true;
                        messages.add("Congrats, you won!!!");
                    }
                } else {
                    messages.add("Great job, you hit your target. Give them what they deserve.");
                }
                enemyShoot(shipBoard, messages);
            } else {
                messages.add("What are you doing? You already shot that place");
            }
        }
        htmlCode(boards, resp, messages, gameOver);
    }

    private void waterAround(int[][] shipBoard, int[] fireLoc) {
        Set<String> checked = new HashSet<>();
        Queue<int[]> toCheck = new LinkedList<>();
        toCheck.add(fireLoc);
        int[][] outlook = new int[][]{
                new int[]{1, 1},
                new int[]{1, 0},
                new int[]{1, -1},
                new int[]{0, 1},
                new int[]{0, -1},
                new int[]{-1, 1},
                new int[]{-1, 0},
                new int[]{-1, -1}
        };
        while (!toCheck.isEmpty()) {
            int[] toTest = toCheck.remove();

            int x = toTest[0];
            int y = toTest[1];

            for (int[] out : outlook) {
                int _x = x + out[0];
                int _y = y + out[1];
                if (_x >= 0 && _x <= 9 && _y >= 0 && _y <= 9) {
                    if (shipBoard[_x][_y] == 0) {
                        shipBoard[_x][_y] = 2;
                    }
                    if (shipBoard[_x][_y] == 3) {
                        int[] e = {_x, _y};
                        if (!checked.contains(_x + ":" + _y)) {
                            toCheck.add(e);
                        }
                    }

                }
            }
            checked.add(x + ":" + y);
        }

    }

    protected int[] fire(String fire, List<String> messages) {
        char fireAtX = fire.toLowerCase().trim().charAt(0);
        int helpX = fireAtX - 'a';
        try {
            int fireAtY = Integer.parseInt(fire.substring(1)) - 1;
            if (helpX >= 0 && helpX <= 9 && fireAtY >= 0 && fireAtY <= 9) {
                return new int[]{helpX, fireAtY};
            }
            messages.add("Why'd you shoot out of range? The range is only a - j and 1 - 10.");
            return null;
        } catch (NumberFormatException e) {
            messages.add("Put first letter and then number.");
            return null;
        }
    }

    private boolean sunkenShip(int[][] shipBoard, int[] fireLoc) {
        Set<String> checked = new HashSet<>();
        Queue<int[]> toCheck = new LinkedList<>();
        toCheck.add(fireLoc);

        int[][] outlook = new int[][]{
                new int[]{-1, 0},
                new int[]{1, 0},
                new int[]{0, -1},
                new int[]{0, 1},
        };

        while (!toCheck.isEmpty()) {
            int[] toTest = toCheck.remove();

            int x = toTest[0];
            int y = toTest[1];

            for (int[] out : outlook) {
                int _x = x + out[0];
                int _y = y + out[1];
                if (_x >= 0 && _x <= 9 && _y >= 0 && _y <= 9) {
                    if (shipBoard[_x][_y] == 1) {
                        return false;
                    } else if (shipBoard[_x][_y] == 3) {
                        int[] e = {_x, _y};
                        if (!checked.contains(_x + ":" + _y)) {
                            toCheck.add(e);
                        }
                    }
                }
            }

            checked.add(x + ":" + y);
        }
        return true;
    }

    private int[][] boardGenerate() {
        int[][] shipBoard = new int[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                shipBoard[i][j] = 0;
            }
        }
        ShipPlace.makeBoard(shipBoard);
        return shipBoard;
    }

    private void enemyShoot(int[][] shipBoard, List<String> messages) {
        while (true) {
            int x = rng(0, 9);
            int y = rng(0, 9);
            if (shipBoard[x][y] == 0) {
                shipBoard[x][y] = 2;
                break;
            } else if (shipBoard[x][y] == 1) {
                shipBoard[x][y] = 3;
                if (sunkenShip(shipBoard, new int[]{x, y})) {
                    if (gameOver(shipBoard)) {
                        messages.add("Game over!!! You lost, but don't worry. You'll get them next time.");
                    } else
                        messages.add("Oh no, we lost one of our best ships.");
                } else {
                    messages.add("Ouch, we're hit.");
                }
                break;
            }
        }

    }

    private void enemyShootImpossible(int[][] shipBoard, List<String> messages) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (shipBoard[i][j] == 1) {
                    shipBoard[i][j] = 3;
                    if (gameOver(shipBoard)) {
                        messages.add("Did you think you could win this?");
                    } else {
                        messages.add("You really thought this was gonna be easy?");
                    }
                    return;
                }
            }
        }
    }

    private boolean gameOver(int[][] shipBoard) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (shipBoard[i][j] == 1) {
                    return false;
                }
            }
        }
        return true;
    }

    private void htmlCode(Boards boards, HttpServletResponse resp, List<String> messages, boolean gameOver) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<html>\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\"/>\n" +
                "    <title>Board Games</title>\n" +
                " <style><!--\n" +
                "    .ship{background: black;}\n" +
                "    .water{background: aqua; color: aqua;}\n" +
                "    .default{background: lightgrey; color: lightgrey;}\n" +
                "    .hitship{background: red; color: red;}\n" +
                "    .hitwater{background: blue; color: blue;}\n" +
                "    .number{background: white; color: black;}\n" +
                "    --></style>" +
                "</head>\n" +
                "<body>\n");
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        html.append("<table border=\"10\" cellspacing=\"0\" cellpadding=\"0\">\n");
        int k = 0;
        int a = 'a' - 1;
        int b = 0;
        for (int l = 0; l < 11; l++) {
            html.append("<td width=\"17\" align=\"center\" class=\"number\">");
            if (b == 0) {
                html.append("");
            } else {
                html.append(b);
            }
            html.append("</td>");
            b++;
        }
        html.append("<tr>");
        for (int i = 0; i < 10; i++) {
            a++;
            html.append("<tr>");
            html.append("<td width=\"17\" align=\"center\" class=\"number\">");
            html.append((char) a);
            html.append("</td>");
            for (int j = 0; j < 10; j++) {
                String style;
                switch (boards.shipBoard[i][j]) {
                    case 0:
                        style = "water";
                        break;
                    case 1:
                        style = "ship";
                        break;
                    case 2:
                        style = "hitwater";
                        break;
                    case 3:
                        style = "hitship";
                        break;
                    default:
                        style = "default";
                }

                html.append("<td width=\"17\" align=\"center\" class=\"").append(style).append("\">");
                html.append("</td>");
            }
            html.append("</tr>");
            html.append("\n");
        }
        html.append("</table>");
        html.append("<table border=\"10\" cellspacing=\"0\" cellpadding=\"0\">\n");
        a = 'a' - 1;
        b = 0;
        for (int l = 0; l < 11; l++) {
            html.append("<td width=\"17\" align=\"center\" class=\"number\">");
            if (b == 0) {
                html.append("");
            } else {
                html.append(b);
            }
            html.append("</td>");
            b++;
        }
        for (int i = 0; i < 10; i++) {
            a++;
            html.append("<tr>");
            html.append("<tr>");
            html.append("<td width=\"17\" align=\"center\" class=\"number\">");
            html.append((char) a);
            for (int j = 0; j < 10; j++) {
                String style;
                switch (boards.enemyBoard[i][j]) {
                    case 2:
                        style = "hitwater";
                        break;
                    case 3:
                        style = "hitship";
                        break;
                    default:
                        style = "default";
                }

                html.append("<td width=\"17\" align=\"center\" class=\"").append(style).append("\">");
                html.append("</td>");
            }
            html.append("</tr>");
            html.append("\n");
        }
        html.append("</table>");
        for (String s : messages) {
            html.append("<h3>").append(s).append("</h3>\n ");
        }
        if (!gameOver) {
            html.append("<form action=\"/ships/fire\"  method=\"post\">\n" +
                    "    <label for=\"fire\"><b>Put in coords: </b></label>\n" +
                    "    <input type=\"text\" name=\"fire\" placeholder=\"Enter where to shoot\" id=\"fire\" required>\n" +
                    "    <button type=\"FIRE\">Fire</button>\n" +
                    "</form>");
        }
        html.append("</body>\n" +
                "</html>");
        resp.getWriter().println(html);
    }
}