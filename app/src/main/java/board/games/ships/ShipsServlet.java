package board.games.ships;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ShipsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {



        int[][] shipBoard = (int[][]) request.getSession().getAttribute("shipBoard");
        int[][] enemyBoard = (int[][]) request.getSession().getAttribute("enemyBoard");
        if (shipBoard == null) {
            shipBoard = boardGenerate();
            enemyBoard = boardGenerate();
            request.getSession().setAttribute("shipBoard", shipBoard);
            request.getSession().setAttribute("enemyBoard", enemyBoard);
        }
       htmlCode(shipBoard, enemyBoard, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fire = req.getParameter("fire");
        int[] fireLoc = fire(fire);
        int[][] shipBoard = (int[][]) req.getSession().getAttribute("shipBoard");
        int[][] enemyBoard = (int[][]) req.getSession().getAttribute("enemyBoard");
        if (fireLoc == null) {
            System.out.println("error");
        }else {
            if (enemyBoard[fireLoc[0]][fireLoc[1]] == 0) {
                enemyBoard[fireLoc[0]][fireLoc[1]] = 2;
            }else if (enemyBoard[fireLoc[0]][fireLoc[1]] == 1) {
                enemyBoard[fireLoc[0]][fireLoc[1]] = 3;
            }
        }
        htmlCode(shipBoard, enemyBoard, resp);
    }
    protected int[] fire(String fire) {
        char fireAtX = fire.toLowerCase().charAt(0);
        int helpX = fireAtX - 'a';
        int fireAtY = Integer.parseInt(fire.substring(1)) - 1;
        if(helpX >= 0 && helpX <= 9 && fireAtY >= 0 && fireAtY <= 9) {
            return new int[]{helpX, fireAtY};
        }
        return null;
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

    private void htmlCode (int [][] shipBoard, int[][] enemyBoard, HttpServletResponse resp) throws IOException {
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
                "    --></style>" +
                "</head>\n" +
                "<body>\n");
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        html.append("<table border=\"10\" cellspacing=\"0\" cellpadding=\"0\">\n");
        for (int i = 0; i < 10; i++) {
            html.append("<tr>");
            for (int j = 0; j < 10; j++) {
                String style = shipBoard[i][j] == 1 ? "ship" : "water";

                html.append("<td width=\"17\" align=\"center\" class=\"").append(style).append("\">");
                html.append("_");
                html.append("</td>");
            }
            html.append("</tr>");
            html.append("\n");
        }
        html.append("</table>");
        html.append("<table border=\"10\" cellspacing=\"0\" cellpadding=\"0\">\n");
        for (int i = 0; i < 10; i++) {
            html.append("<tr>");
            for (int j = 0; j < 10; j++) {
                String style;
                switch (enemyBoard[i][j]) {
                    case 2 : style = "hitwater"; break;
                    case 3 : style = "hitship"; break;
                    default:
                        style = "default";
                }

                html.append("<td width=\"17\" align=\"center\" class=\"").append(style).append("\">");
                html.append("_");
                html.append("</td>");
            }
            html.append("</tr>");
            html.append("\n");
        }
        html.append("</table>");
        html.append("<h1>Fire</h1>\n" +
                "<form action=\"/ships/fire\"  method=\"post\">\n" +
                "    <label for=\"fire\"><b>Fire: </b></label>\n" +
                "    <input type=\"text\" name=\"fire\" placeholder=\"Enter where to shoot\" id=\"fire\" required>\n" +
                "    <button type=\"FIRE\">Fire</button>\n" +
                "</form>");
        html.append("</body>\n" +
                "</html>");
        resp.getWriter().println(html);
    }
}