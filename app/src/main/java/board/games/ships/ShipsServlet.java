package board.games.ships;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static board.games.utils.Rng.rng;

public class ShipsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        int max = Integer.parseInt(request.getParameter("max"));
//        int min = Integer.parseInt(request.getParameter("min"));

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

        int[][] shipBoard = new int[10][10];
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                shipBoard [i][j] = 0;
            }
        }
        ShipPlace.makeBoard(shipBoard);
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        html.append("<table border=\"10\" cellspacing=\"0\" cellpadding=\"0\">\n");
        for(int i = 0; i < 10; i++) {
            html.append("<tr>");
            for(int j = 0; j < 10; j++) {
                if(shipBoard[i][j] == 1) {
                    html.append("<td width=\"17\" align=\"center\" class=\"black\">");
                    html.append(shipBoard[i][j]);
                    html.append("</td>");
                }else {
                    html.append("<td width=\"17\" align=\"center\" class=\"blue\">");
                    html.append(shipBoard[i][j]);
                    html.append("</td>");
                }
            }
            html.append("</tr>");
            html.append("\n");
        }
        html.append("</table>");
        html.append("</body>\n" +
                "</html>");
        response.getWriter().println(html);
    }
}