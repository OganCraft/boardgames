package board.games.ships;

import static board.games.utils.Rng.rng;

public class ShipPlace {
    private static boolean placeShipPriv(int[][] shipBoard, int mX, int mY, Ships type) {
        int x = rng(9, mX);
        int y = rng(9 - mY, 0);
        switch (type) {
            case BOAT:
                if (shipBoard[x][y] != 0) {
                    return false;
                }
                break;
            case SUBMARINE:
                if (shipBoard[x][y] != 0 || shipBoard[x][y + 1] != 0) {
                    return false;
                }
                break;
            case STEAMBOAT:
                if (shipBoard[x][y] != 0 || shipBoard[x][y + 1] != 0 || shipBoard[x][y + 2] != 0 || shipBoard[x - 1][y + 1] != 0) {
                    return false;
                }
                break;
            case CRUISER:
                if (shipBoard[x][y] != 0 || shipBoard[x][y + 1] != 0 || shipBoard[x][y + 2] != 0 || shipBoard[x - 1][y + 1] != 0 || shipBoard[x][y + 3] != 0 || shipBoard[x - 1][y + 3] != 0 || shipBoard[x][y + 4] != 0) {
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
                case BOAT : if (help == 0) {
                    return true;
                }
                break;
                case SUBMARINE : if (help == 1) {
                    return true;
                }
                break;
                case STEAMBOAT : if (help == 3) {
                    return true;
                }
                break;
                case CRUISER : if (help == 6) {
                    return true;
                }
                break;
            }
            help += 1;
        }
        return  true;
    }


    private static void placeShip(int[][] shipBoard, Ships type) {
        int mX = 0;
        int mY = 0;
        switch (type) {
            case BOAT:
                mY = 0;
                mX = 0;
                break;
            case SUBMARINE:
                mY = 1;
                mX = 0;
                break;
            case STEAMBOAT:
                mY = 2;
                mX = 1;
                break;
            case CRUISER:
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
    public static void makeBoard(int[][] shipBoard) {
        ShipPlace.placeShip(shipBoard, Ships.CRUISER);
        ShipPlace.placeShip(shipBoard, Ships.STEAMBOAT);
        ShipPlace.placeShip(shipBoard, Ships.STEAMBOAT);
        ShipPlace.placeShip(shipBoard, Ships.SUBMARINE);
        ShipPlace.placeShip(shipBoard, Ships.SUBMARINE);
        ShipPlace.placeShip(shipBoard, Ships.SUBMARINE);
        ShipPlace.placeShip(shipBoard, Ships.BOAT);
        ShipPlace.placeShip(shipBoard, Ships.BOAT);
        ShipPlace.placeShip(shipBoard, Ships.BOAT);
        ShipPlace.placeShip(shipBoard, Ships.BOAT);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (shipBoard[i][j] == 2) {
                    shipBoard[i][j] = 0;
                }
            }
        }
    }
}
