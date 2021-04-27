package board.games.wizard;

import board.user.User;

import java.util.List;
import java.util.Map;

class EndOfRoundJson extends EventJson {
    private User winner;
    private List<GetStateJson.PlayedCard> playedCards;

    public EndOfRoundJson(User winner, Map<String, Card> playedCards) {
        super("end-of-round");
        this.winner = winner;
        this.playedCards = GetStateJson.toPlayedCards(playedCards);
    }
}
