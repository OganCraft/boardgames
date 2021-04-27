package board.games.wizard;

import board.user.User;

import java.util.List;

public class NewRoundJson extends EventJson {
    private String onTurn;
    private List<User> players; // order of players in this round
    private List<Card> myCards;
    private Card trump;

    public NewRoundJson(User user, WizardState state) {
        super("new-round");
        this.onTurn = state.getOnTurn().getId();
        this.players = state.getPlayers();
        this.myCards = state.getCardsInHand().get(user.getId());
        this.trump = state.getTrump();
    }
}
