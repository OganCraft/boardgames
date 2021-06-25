package board.games.covernames;

import board.room.Room;
import board.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static board.utils.Rng.rng;

class GameState {
    private List<List<String>> cards = new ArrayList<>();
    private int[][] hint = new int[5][5];
    private List<User> blueTeam;
    private List<User> redTeam;
    private List<User> hintGuys;
    private String teamOnTurn;

    public GameState(Room room) {
        List<Integer> hintHelp = new ArrayList<>();
        blueTeam = new ArrayList<>();
        redTeam = new ArrayList<>();
        hintGuys = new ArrayList<>();
        // 1 = modrý tým, 2 = červený tým, 3 = vrah, 4 = náhodný kolemjdoucí
        int blueCount = 8;
        int redCount = 8;
        int blackCount = 1;
        int whiteCount = 25 - (blueCount + redCount + blackCount);
        if (rng(0, 1) == 1) {
            blueCount += 1;
            teamOnTurn = "blue";
        } else {
            redCount += 1;
            teamOnTurn = "red";
        }
        for (int i = 0; i < blueCount; i++) {
            hintHelp.add(1);
        }
        for (int i = 0; i < redCount; i++) {
            hintHelp.add(2);
        }
        hintHelp.add(3);
        for (int i = 0; i < whiteCount; i++) {
            hintHelp.add(4);
        }
        Collections.shuffle(hintHelp);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                hint[i][j] = hintHelp.get(5 * i + j);
            }
        }
        List<String> cardsHelp = new ArrayList<>(CardsAvailable.getCardsAvailable());
        Collections.shuffle(cardsHelp);
        for (int i = 0; i < 5; i++) {
            List<String> sublist = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                sublist.add(cardsHelp.get(i * 5 + j));
            }
            cards.add(sublist);
        }
        //  just for testing, in the actual game take one person from each team
        hintGuys = new ArrayList<>(room.players());
        hintGuys.remove(0);
    }

    public String getTeamOnTurn() {
        return teamOnTurn;
    }

    public int[][] getHint() {
        return hint;
    }

    public List<List<String>> getCards() {
        return cards;
    }

    public List<User> getBlueTeam() {
        return blueTeam;
    }

    public List<User> getRedTeam() {
        return redTeam;
    }

    public List<User> getHintGuys() {
        return hintGuys;
    }
}
