package board.games.utils;

public class Rng {
    public static int rng(int max, int min) {
        int m = min - 1;
        int number = (int) (Math.floor(Math.random() * (max - m)) + 1 + m);
        return number;
    }
}
