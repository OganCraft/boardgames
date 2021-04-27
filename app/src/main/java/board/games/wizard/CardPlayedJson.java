package board.games.wizard;

import java.util.List;

/**
 * Information for a waiting player for its turn.
 */
public class CardPlayedJson extends EventJson {
    private String onTurn;
    private List<GetStateJson.PlayedCard> playedCards;

    public CardPlayedJson(WizardState state) {
        super("card-played");
        this.onTurn = state.getOnTurn().getId();
        this.playedCards = GetStateJson.toPlayedCards(state.getPlayedCards());
    }
}
