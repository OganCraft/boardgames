package board.games.ships;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ShipsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuilder html = new StringBuilder();
        html.append("<html>\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\"/>\n" +
                "    <title>Board Games</title>\n" +
                " <style><!--\n" +
                "    .black{\n" +
                "    background: black;}\n" +
                "    .blue{\n" +
                "    background: aqua; color: aqua;}\n" +
                "    --></style>" +
                "</head>\n" +
                "<body>\n");


        int[][] shipBoard = (int[][]) request.getSession().getAttribute("shipBoard");
        if (shipBoard == null) {
            shipBoard = new int[10][10];
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    shipBoard[i][j] = 0;
                }
            }
            ShipPlace.makeBoard(shipBoard);
            request.getSession().setAttribute("shipBoard", shipBoard);
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        html.append("<table border=\"10\" cellspacing=\"0\" cellpadding=\"0\">\n");
        for (int i = 0; i < 10; i++) {
            html.append("<tr>");
            for (int j = 0; j < 10; j++) {
                String style = shipBoard[i][j] == 1 ? "black" : "blue";

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
                html.append("<td width=\"17\" align=\"center\" class=\"blue").append("\">");
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
        response.getWriter().println(html);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fire = req.getParameter("fire");
        resp.getWriter().println("ccvbnb");
    }
}