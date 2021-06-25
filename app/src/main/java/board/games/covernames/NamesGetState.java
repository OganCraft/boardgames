package board.games.covernames;

import board.user.User;

import java.util.ArrayList;
import java.util.List;

class NamesGetState {
    private String event = "get-state";
    private String user;
    private String userId;
    private boolean hint;
    private String team;
    private String teamOnTurn;
    private List<String> red;
    private List<String> blue;




    public NamesGetState(User user, GameState state) {
        this.user = user.getName();
        this.userId = user.getId();
        if (state.getHintGuys().contains(user)) {
            hint = true;
        }else {
            hint = false;
        }
        if (state.getBlueTeam().contains(user)) {
            team = "modrý";
        }else {
            team = "červený";
        }
        this.teamOnTurn = state.getTeamOnTurn();
        blue = new ArrayList<>();
        for (int i = 0; i < state.getBlueTeam().size(); i++) {
            blue.add(state.getBlueTeam().get(i).getName());
        }
        red = new ArrayList<>();
        for (int i = 0; i < state.getRedTeam().size(); i++) {
            red.add(state.getRedTeam().get(i).getName());
        }
    }
}
