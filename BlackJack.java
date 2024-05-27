import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class BlackJack {
    public class Card {
        String value;
        String type;

        Card (String val, String type) {
            this.value = value;
            this.type = type;
        }
    }

    ArrayList<Card> deck;

    BlackJack() {
        startGame();
    }

    public void startGame() {
        buildDeck();
    }

    public void buildDeck() {
        deck = new ArrayList<Card>();
        String[] values = {"A", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] type = {"C", "D", "H", "S"};

        for(int i = 0; i < type.length; i++) {
            for (int j = 0; j < values.length; j++) {
                Card card = new Card(values[j], type[i]);
                deck.add(card);
            }
        }
        System.out.println("Колода карт:");
        System.out.println(deck);
    }
}
