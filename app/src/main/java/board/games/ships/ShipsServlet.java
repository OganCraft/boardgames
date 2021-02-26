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
        int[][] shipBoard = new int[10][10];
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                shipBoard [i][j] = 0;
            }
        }

        ShipPlace.placeShip(shipBoard, 3);
        ShipPlace.placeShip(shipBoard, 2);
        ShipPlace.placeShip(shipBoard, 2);
        ShipPlace.placeShip(shipBoard, 1);
        ShipPlace.placeShip(shipBoard, 1);
        ShipPlace.placeShip(shipBoard, 1);
        ShipPlace.placeShip(shipBoard, 0);
        ShipPlace.placeShip(shipBoard, 0);
        ShipPlace.placeShip(shipBoard, 0);
        ShipPlace.placeShip(shipBoard, 0);
//        shipBoard[rng(9, 0)][rng(9, 0)] = 1;
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
//        response.getWriter().println("{ \"random\": \"" + rng(max , min) + "\"}");
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                response.getWriter().print(shipBoard[i][j] + " ");
            }
            response.getWriter().println();
        }
    }
}