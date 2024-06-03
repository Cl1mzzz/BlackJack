import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.border.AbstractBorder;

public class BlackJack {
    public class Card {
        String value;
        String type;

        Card (String value, String type) {
            this.value = value;
            this.type = type;
        }
        public String toString() {
            return value + "-" + type;
        }
        public int getValue() {
            if ("AJQK". contains(value)){
                if (value == "A"){
                    return 11;
                }
                return 10;
            }
            return Integer.parseInt(value);
        }
        public boolean isAce(){
            return value == "A";
        }
        public String getImagePath(){
            return "/cards/" + toString() + ".png";
        }
    }

    ArrayList<Card> deck;
    Random rand = new Random();

    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount;

    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;


    int boardWidth = 1000;
    int boardHeight = 600;

    int cardWidth = 110;
    int cardHeight = 154;

    JFrame frame = new JFrame("BlackJack");
    JPanel gamePanel;
    JPanel buttonPanel;
    JButton hitButton = new JButton("Hit");
    JButton stayButton = new JButton("Stay");
    JButton newGameButton = new JButton("New Game");

    BlackJack() {
        gamePanel = new ImagePanel("D:\\Course_Work\\BlackJack\\cards\\Background-Image.jpg") {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                try {
                    Image hiddenCardImg = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
                    if (!stayButton.isEnabled()){
                        hiddenCardImg = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())).getImage();
                    }
                    g.drawImage(hiddenCardImg, 20, 20, cardWidth, cardHeight, null);

                    for (int i = 0; i < dealerHand.size(); i++) {
                        Card card = dealerHand.get(i);
                        Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                        g.drawImage(cardImg, cardWidth + 30 + (cardWidth + 10) * i, 20, cardWidth, cardHeight, null);
                    }

                    for (int i = 0; i < playerHand.size(); i++) {
                        Card card = playerHand.get(i);
                        Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                        g.drawImage(cardImg,20 + (cardWidth + 10) * i, 320, cardWidth, cardHeight, null);
                    }

                    String dealer = "Dealer";
                    String player = "Player";

                    g.setFont(new Font("Arial", Font.PLAIN, 20));
                    g.setColor(Color.white);
                    g.drawString(dealer, 30, 210);
                    g.drawString(player, 30, 300);

                    if (!stayButton.isEnabled()){
                        dealerSum = reduceDealerAce();
                        playerSum = reducePlayerAce();
                        System.out.println("Stay: ");
                        System.out.println(dealerSum);
                        System.out.println(playerSum);
                        newGameButton.setVisible(true);

                        String message = "";
                        if (playerSum > 21){
                            message = "Loss";
                        }
                        else if (dealerSum > 21) {
                            message = "Victory";
                        }
                        else if (playerSum == dealerSum) {
                            message = "Draw";
                        }
                        else if (playerSum > dealerSum) {
                            message = "Victory";
                        }
                        else if (playerSum < dealerSum){
                            message = "Loss";
                        }

                        g.setFont(new Font("Arial", Font.PLAIN, 30));
                        g.setColor(Color.white);
                        g.drawString(message, 220, 250);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        buttonPanel = new ImagePanel("D:\\Course_Work\\BlackJack\\cards\\Background-Image2.jpg");

        startGame();

        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        hitButton.setFocusable(false);
        hitButton.setPreferredSize(new Dimension(110, 40));
        hitButton.setBackground(new Color(97, 0, 153));
        hitButton.setBorder(new RoundedBorder(35, 5, 97, 0, 153));
        hitButton.setContentAreaFilled(false);
        hitButton.setOpaque(false);
        hitButton.setFocusPainted(false);
        hitButton.setForeground(new Color(255, 255, 255));
        hitButton.setFont(new Font("Arial", Font.BOLD, 16));
        hitButton.addActionListener(e -> System.out.println("Hit button clicked"));

        stayButton.setFocusable(false);
        stayButton.setPreferredSize(new Dimension(120, 40));
        stayButton.setBackground(new Color(255, 215, 0));
        stayButton.setBorder(new RoundedBorder(35, 5, 255, 215, 0));
        stayButton.setContentAreaFilled(false);
        stayButton.setOpaque(false);
        stayButton.setFocusPainted(false);
        stayButton.setForeground(new Color(255, 255, 255));
        stayButton.setFont(new Font("Arial", Font.BOLD, 16));
        stayButton.addActionListener(e -> System.out.println("Stay button clicked"));

        newGameButton.setFocusable(false);
        newGameButton.setPreferredSize(new Dimension(170, 40));
        newGameButton.setBackground(new Color(155, 215, 0));
        newGameButton.setBorder(new RoundedBorder(35, 5, 22, 37, 199));
        newGameButton.setContentAreaFilled(false);
        newGameButton.setOpaque(false);
        newGameButton.setFocusPainted(false);
        newGameButton.setForeground(new Color(255, 255, 255));
        newGameButton.setFont(new Font("Arial", Font.BOLD, 16));
        newGameButton.setVisible(false);

        buttonPanel.add(hitButton);
        buttonPanel.add(stayButton);
        buttonPanel.add(newGameButton);
        frame.add(gamePanel);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        hitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Card card = deck.remove(deck.size() - 1);
                playerSum += card.getValue();
                playerAceCount += card.isAce()? 1 : 0;
                playerHand.add(card);
                if (reducePlayerAce() >= 21){
                    hitButton.setEnabled(false);
                }
                gamePanel.repaint();
            }
        });

        stayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hitButton.setEnabled(false);
                stayButton.setEnabled(false);


                while (dealerSum < 17){
                    Card card = deck.remove(deck.size()-1);
                    dealerSum += card.getValue();
                    dealerAceCount += card.isAce() ? 1 : 0;
                    dealerHand.add(card);
                }
                gamePanel.repaint();
            }
        });

        newGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame();
                gamePanel.repaint();
            }
        });

        gamePanel.repaint();
    }

    public void startGame() {
        buildDeck();
        shuffleDeck();

        dealerHand = new ArrayList<Card>();
        dealerSum = 0;
        dealerAceCount = 0;
        hiddenCard = deck.remove(deck.size()-1);
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card card = deck.remove(deck.size()-1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        System.out.println("Дилер:");
        System.out.println(hiddenCard);
        System.out.println(dealerHand);
        System.out.println(dealerSum);
        System.out.println(dealerAceCount);

        playerHand = new ArrayList<Card>();
        playerSum = 0;
        playerAceCount = 0;

        for(int i = 0; i < 2; i++){
            card = deck.remove(deck.size()-1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }

        System.out.println("Гравець:");
        System.out.println(playerHand);
        System.out.println(playerSum);
        System.out.println(playerAceCount);

        hitButton.setEnabled(true);
        stayButton.setEnabled(true);
        newGameButton.setVisible(false);

    }

    public void buildDeck() {
        deck = new ArrayList<Card>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
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

    public void shuffleDeck() {
        for (int i = 0; i < deck.size(); i++) {
            int j = rand.nextInt(deck.size());
            Card currCard = deck.get(i);
            Card randomCard = deck.get(j);
            deck.set(i, randomCard);
            deck.set(j, currCard);
        }
        System.out.println("Колода після тосування");
        System.out.println(deck);
    }

    private class ImagePanel extends JPanel {
        private BufferedImage backgroundImage;

        public ImagePanel(String imagePath) {
            backgroundImage = loadImage(imagePath);
        }

        private BufferedImage loadImage(String imagePath) {
            try {
                return ImageIO.read(new File(imagePath));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    class RoundedBorder extends AbstractBorder {
        private int radius;
        private Color color;
        private int thickness;

        RoundedBorder(int radius, int thickness, int r, int g, int b) {
            this.radius = radius;
            this.thickness = thickness;
            this.color = new Color(r, g, b); // Создание цвета с использованием RGB значений
        }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(this.color); // Используем цвет границы, заданный в конструкторе
            g2d.setStroke(new BasicStroke(thickness)); // Установка толщины границы
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }
        @Override
        public Insets getBorderInsets(Component c) {
            int value = this.radius + this.thickness;
            return new Insets(value, value, value, value);
        }
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            int value = this.radius + this.thickness;
            insets.left = insets.top = insets.right = insets.bottom = value;
            return insets;
        }
    }

    public int reducePlayerAce() {
        while (playerSum > 21 && playerAceCount > 0){
            playerSum -= 10;
            playerAceCount -= 1;
        }
        return playerSum;
    }

    public int reduceDealerAce() {
        while (dealerSum > 21 && dealerAceCount > 0){
            dealerSum -= 10;
            dealerAceCount -= 1;
        }
        return dealerSum;
    }
}
