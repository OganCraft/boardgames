package board.games.wizard;

import board.user.User;

import java.util.List;
import java.util.Map;

/**
 * This class contains game rules.
 */
class WizardRules {
    /**
     * During one round, validate that the user can play the card.
     * Check the cards the user have, check against rules.
     *
     * @param user  playing user
     * @param state current game state
     * @param card  a card played
     * @return <tt>null</tt> if everything is ok, else an error message
     */
    public static String playCard(User user, WizardState state, Card card) {
        List<Card> cards = state.getCardsInHand().get(user.getId());
        if (!cards.contains(card))
            return "Tuto kartu hráč nemá v ruce: " + card;

        Map<String, Card> actualRoundCards = state.getActualRoundCards();
        Card.Color roundColor = findFirstCardWithColor(actualRoundCards);

        if (card.isCommon() && roundColor != null && !card.getColor().equals(roundColor)) {
            // follow the color rule
            // check user's cards in hand if there was no other possibility
            // todo
        }

        // todo: check other rules

        state.getCardsInHand().get(user.getId()).remove(card);

        actualRoundCards.put(user.getId(), card);

        state.setEndOfRound(actualRoundCards.size() == state.getPlayers().size());

        if (state.isEndOfRound()) {
            WizardRules.closeTheRound(state);
        } else {
            state.nextOnTurn();
        }

        return null;
    }

    /**
     * Find out the round winner. Update score. Update state.
     *
     * @param state current game state to update
     */
    static void closeTheRound(WizardState state) {
        Map<String, Card> actualRoundCards = state.getActualRoundCards();
        Card.Color trumpColor = state.getTrump() != null ? state.getTrump().getColor() : null;
        Card.Color roundColor = null;

        // winner - userId
        String winnerId = null;
        Card winnerCard = null;

        for (Map.Entry<String, Card> entry : actualRoundCards.entrySet()) {
            String userId = entry.getKey();
            Card card = entry.getValue();

            if (winnerId == null || card.isZauberer()) {
                winnerId = userId;
                winnerCard = card;
            }

            // no one can play higher
            if (winnerCard.isZauberer())
                break;

            // Narr (joker) always looses
            if (card.isNarr())
                continue;

            // following conditions follow the rule:
            // check if actual card is lower than the current winning card - then continue with next card
            // if no check apply, then the card is higher than current winning card, update the winner

            // fight of trumps!
            if (card.getColor().equals(trumpColor) && winnerCard.getColor().equals(trumpColor)
                    && card.getValue() < winnerCard.getValue())
                continue;

            // no trump card can beat a trump of any value
            if (!card.getColor().equals(trumpColor) && winnerCard.getColor().equals(trumpColor))
                continue;

            if (roundColor == null && card.isCommon())
                roundColor = card.getColor();

            // fight of cards having the round color
            if (card.getColor().equals(roundColor) && winnerCard.getColor().equals(roundColor)
                    && card.getValue() < winnerCard.getValue())
                continue;

            // a card with a color different to the round color has no chance
            if (!card.getColor().equals(roundColor))
                continue;

            winnerId = userId;
            winnerCard = card;
        }

        state.setRoundWinner(state.getUser(winnerId));
        // todo update score
    }

    private static Card.Color findFirstCardWithColor(Map<String, Card> actualRoundCards) {
        for (Card card : actualRoundCards.values()) {
            if (card.isCommon())
                return card.getColor();
        }
        return null;
    }
}
