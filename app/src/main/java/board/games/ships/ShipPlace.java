package board.games.ships;

import static board.games.utils.Rng.rng;

public class ShipPlace {
    private static boolean placeShipPriv(int[][] shipBoard) {
        int x = rng(9, 0);
        int y = rng(9, 0);
        if (shipBoard[x][y] != 0) {
             return false;
        } else {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (x + i < 0 || x + i > 9 || y + j < 0 || y + j > 9) {
                    } else {
                        shipBoard[x + i][y + j] = 2;
                    }
                }
            }
            shipBoard[x][y] = 1;
        }
        return true;
    }


    public static void placeShip (int [][] shipBoard) {
        while(true) {
            if(placeShipPriv(shipBoard)){
                break;
            }
        }
    }
}
