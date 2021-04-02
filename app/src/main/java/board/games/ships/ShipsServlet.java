package board.games.ships;

import board.room.Room;
import board.user.User;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

import static board.games.utils.Rng.rng;

public class ShipsServlet extends HttpServlet {

    private static final class Boards {
        int[][] shipBoard, enemyBoard;
        String me, enemy;

        public Boards(int[][] shipBoard, int[][] enemyBoard, String me, String enemy) {
            this.shipBoard = shipBoard;
            this.enemyBoard = enemyBoard;

            this.me = me;
            this.enemy = enemy;
        }
    }

    public static class OneRow {
        private String letter;
        private List<Integer> columns;

        public OneRow(String letter, List<Integer> values) {
            this.letter = letter;
            this.columns = values;
        }

        public String getLetter() {
            return letter;
        }

        public List<Integer> getColumns() {
            return columns;
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        Room room = (Room) request.getSession().getAttribute("room");
        User user = (User) request.getSession().getAttribute("user");

        String path = request.getRequestURI();
        if ("/ships/start".equals(path)) {
            initGame(room);
            room.parameters().put("onTurn", user.getName());
            resp.sendRedirect("/ships");
            return;
        }

        Boards boards = getBoards(room, user);
        boolean myTurn = boards.me.equals(room.parameters().get("onTurn"));
        htmlCode(boards, myTurn, resp, request, Collections.emptyList(), room);
    }

    private void initGame(Room room) {
        List<String> playerNames = new ArrayList<>();
        for (User player : room.players()) {
            room.parameters().put(player.getName(), boardGenerate());
            playerNames.add(player.getName());
        }
        room.parameters().put("playerNames", playerNames);
    }

    private Boards getBoards(Room room, User user) {
        // create copy of players, so we can freely modify the collection
        List<String> players = new ArrayList((List<String>) room.parameters().get("playerNames"));

        int[][] shipBoard = (int[][]) room.parameters().get(user.getName());
        players.remove(user.getName());
        // the name which left is the enemy
        String enemy = players.get(0);
        int[][] enemyBoard = (int[][]) room.parameters().get(enemy);

        return new Boards(shipBoard, enemyBoard, user.getName(), enemy);
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

        if (fireLoc != null) {
            if (enemyBoard[fireLoc[0]][fireLoc[1]] == 0) {
                enemyBoard[fireLoc[0]][fireLoc[1]] = 2;
                messages.add("Aw, you missed.");
//                enemyShoot(shipBoard, messages);
            } else if (enemyBoard[fireLoc[0]][fireLoc[1]] == 1) {
                enemyBoard[fireLoc[0]][fireLoc[1]] = 3;
                if (sunkenShip(enemyBoard, fireLoc)) {
                    messages.add("Captain, you are the best. Their ship is sinking.");
                    waterAround(enemyBoard, fireLoc);
                    if (gameOver(enemyBoard)) {
                        room.gameOver();
                        messages.add("Congrats, you won!!!");
                    }
                } else {
                    messages.add("Great job, you hit your target. Give them what they deserve.");
                }
//                enemyShoot(shipBoard, messages);
            } else {
                messages.add("What are you doing? You already shot that place");


            }
        }
        htmlCode(boards, false, resp, req, messages, room);
        room.parameters().put("onTurn", boards.enemy);
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

    private void htmlCode(Boards boards, boolean myTurn, HttpServletResponse resp, HttpServletRequest req, List<String> messages, Room room) throws IOException, ServletException {
        List<String> x = new ArrayList<>();
        List<OneRow> y = new ArrayList<>();
        List<OneRow> z = new ArrayList<>();
        // create rows
        for(int i = 0; i < 10; i++) {
            // one column for the first row
            x.add(String.valueOf(i+1));
            // columns of the other rows
            String letter = String.valueOf((char)('a' + i));
            List<Integer> columns = new ArrayList<>();
            List<Integer> enemyColumns = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                columns.add(boards.shipBoard[i][j]);
                enemyColumns.add(boards.enemyBoard[i][j]);
            }

            y.add(new OneRow(letter, columns));
            z.add(new OneRow(letter, enemyColumns));
        }
        req.setAttribute("columns", x);
        req.setAttribute("rows", y);
        req.setAttribute("enemyRows", z);
        req.setAttribute("myTurn", myTurn);
        getServletContext().getRequestDispatcher("/th/ships/ships.th").forward(req, resp);
    }
}
