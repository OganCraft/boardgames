package board.games.chocowitch;

import java.util.ArrayList;
import java.util.Collections;

public class CardDeckMake {
    public static ArrayList<String> deckMake() {
        ArrayList<String> deck = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            deck.add("witch1");
            deck.add("witch2");
            deck.add("witch3");
            deck.add("witch4");
        }
        for (int i = 0; i < 7; i++) {
            deck.add("butter");
            deck.add("cacao");
            deck.add("milk");
            deck.add("nuts");
            deck.add("sugar");
            deck.add("vanilla");
        }
        Collections.shuffle(deck);
        return deck;
    }
}
