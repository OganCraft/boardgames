package board.games.ships;

import static board.games.utils.Rng.rng;

public class ShipPlace {
    private static boolean placeShipPriv(int[][] shipBoard, int mX, int mY, int type) {
        int x = rng(9, mX);
        int y = rng(9 - mY, 0);
        switch (type) {
            case 0:
                if (shipBoard[x][y] != 0) {
                    return false;
                }
                break;
            case 1:
                if (shipBoard[x][y] != 0 || shipBoard[x][y + 1] != 0) {
                    return false;
                }
                break;
            case 2:
                if (shipBoard[x][y] != 0 || shipBoard[x][y + 1] != 0 || shipBoard[x][y + 2] != 0 || shipBoard[x + 1][y + 1] != 0) {
                    return false;
                }
                break;
            case 3:
                if (shipBoard[x][y] != 0 || shipBoard[x][y + 1] != 0 || shipBoard[x][y + 2] != 0 || shipBoard[x + 1][y + 1] != 0 || shipBoard[x][y + 3] != 0 || shipBoard[x + 1][y + 3] != 0 || shipBoard[x][y + 4] != 0) {
                    return false;
                }
                break;
        }
        int help = 0;
        for (int h = 0; h < 7; h++) {
            switch (help) {
                case 0:
                    break;
                case 1:
                    y += 1;
                    break;
                case 2:
                    x -= 1;
                    break;
                case 3:
                    y += 1;
                    x += 1;
                    break;
                case 4:
                    y += 1;
                    break;
                case 5:
                    x -= 1;
                    break;
                case 6:
                    y += 1;
                    x += 1;
                    break;
            }
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (x + i < 0 || x + i > 9 || y + j < 0 || y + j > 9) {
                    } else {
                        if (shipBoard[x + i][y + j] == 0) {
                            shipBoard[x + i][y + j] = 2;
                        }
                    }
                }
            }
            shipBoard[x][y] = 1;
            switch (type) {
                case 0 : if (help == 0) {
                    return true;
                }
                break;
                case 1 : if (help == 1) {
                    return true;
                }
                break;
                case 2 : if (help == 3) {
                    return true;
                }
                break;
                case 3 : if (help == 6) {
                    return true;
                }
                break;
            }
            help += 1;
        }
        return  true;
    }


    public static void placeShip(int[][] shipBoard, int type) { // 0 = člun, 1 = ponorka, 3 = parník, 4 = letadlová loď
        int mX = 0;
        int mY = 0;
        switch (type) {
            case 0:
                mY = 0;
                mX = 0;
                break;
            case 1:
                mY = 1;
                mX = 0;
                break;
            case 2:
                mY = 2;
                mX = 1;
                break;
            case 3:
                mY = 4;
                mX = 1;
                break;
        }
        while (true) {
            if (placeShipPriv(shipBoard, mX, mY, type)) {
                break;
            }
        }
    }
}
