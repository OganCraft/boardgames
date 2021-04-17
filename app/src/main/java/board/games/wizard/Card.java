package board.games.wizard;

import java.util.ArrayList;
import java.util.List;

/**
 * A representation of one playing card.
 */
class Card {
    enum Color {
        RED, GREEN, BLUE, YELLOW;

        public String toString() {
            return name().toLowerCase();
        }
    }

    private final int value;
    private final Color color;

    Card(int value, Color color) {
        this.value = value;
        this.color = color;
    }

    boolean isZauberer() {
        return value > 13;
    }

    boolean isNarr() {
        return value < 1;
    }

    public int getValue() {
        return value;
    }

    public String getSign() {
        if (isNarr())
            return "n";
        else if (isZauberer()) {
            return "z";
        } else
            return String.valueOf(getValue());
    }

    public Color getColor() {
        return color;
    }

    public static List<Card> allCards() {
        List<Card> cards = new ArrayList<>(15 * 4);
        for (Card.Color color : Card.Color.values()) {
            for (int i = 0; i <= 14; i++) {
                cards.add(new Card(i, color));
            }
        }
        return cards;
    }

    @Override
    public String toString() {
        return "Card{" +
                "value=" + value +
                ", color=" + color +
                '}';
    }
}
