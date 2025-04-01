import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import javax.swing.*;
import javax.swing.ImageIcon;

public class ImagePanel extends JPanel {

    private HashMap<String, Integer> cardValues = new HashMap<>();
    private static Double[][] locationMap = new Double[64][2];
    private static Double[][][] homeMap = new Double[4][4][2];
    private static Double[][][] startMap = new Double[4][4][2];

    class Card implements Comparable<Card> {
        private String value;
        private boolean front;
        private Image image;
        private final int player;
        private int index;
        private boolean played;
        private int movementTicks;
        private boolean selected = false;

        public Card(int player, int index, String value) {
            this.index = index;
            this.value = value;

            if (player == 2) {
                image = new ImageIcon(getClass().getResource("/images/cardBack.png")).getImage();
            } else if (player > 0) {
                image = new ImageIcon(getClass().getResource("/images/sideCard.png")).getImage();
            } else {
                image = new ImageIcon(getClass().getResource("/images/blankCard.png")).getImage();
            }

            setOpaque(false);
            this.player = player;
        }

        @Override
        public int compareTo(Card o) {
            return cardValues.get(this.value) - cardValues.get(o.value);
        }

        private int getCardHeight() {
            int multiplier = 2;
            if (played) {
                multiplier = 1;
            }
            if (player % 2 == 0) {
                return Math.min(getWidth(), getHeight()) / 4 * multiplier;
            }
            return Math.min(getWidth(), getHeight()) * 5 / 28 * multiplier;
        }
        private int getCardWidth() {
            int multiplier = 2;
            if (played) {
                multiplier = 1;
            }
            if (player % 2 == 0) {
                return Math.min(getWidth(), getHeight()) * 5 / 28 * multiplier;
            }
            return Math.min(getWidth(), getHeight()) / 4 * multiplier;
        }
        private int playedX() {
            if (player % 2 == 0) {
                return getWidth() / 2 - getCardWidth() / 2;
            }
            return ((getWidth() - getCardWidth()) / 2 - getX(false)) * movementTicks / 100 + getX(false);
        }
        private int playedY() {
            if (player % 2 == 1) {
                return getHeight() / 2 - getCardHeight() / 2;
            }
            return ((getHeight() - getCardHeight()) / 2 - getY(false)) * movementTicks / 100 + getY(false);
        }
        private int getX(boolean noPlay) {
            if (played && noPlay) { return playedX(); }
            if (player % 2 == 0) {
                return getWidth() / 2 - getCardWidth() / 2 + index * 40 - 20 * (cardTotals[player] - 1);
            }
            if (player == 3) {
                return getWidth() * 91 / 100;
            }
            return getWidth() * 9 / 100 - getCardWidth();
        }
        private int getY(boolean noPlay) {
            if (played && noPlay) { return playedY(); }
            if (player % 2 == 1) {
                return getHeight() / 2 - getCardHeight() / 2 + index * 40 - 20 * (cardTotals[player] - 1);
            }
            if (player == 2) {
                return getHeight() * 9 / 100 - getCardHeight();
            }
            if (selected) { return getHeight() * 89 / 100; }
            return getHeight() * 91 / 100;
        }
        public void played() {
            played = true;
            image = new ImageIcon(getClass().getResource("/images/cardBack.png")).getImage();
        }
        public boolean draw(Graphics g, ImageObserver observer) {
            g.drawImage(image, getX(true), getY(true), getCardWidth(), getCardHeight(), observer);
            if (played) {
                cardSelect = false;
                if (movementTicks == 100) {
                    pile.value = this.value;
                    if (value.compareTo("7") == 0) {
                        sevenMovesRemaining = 7;
                        setSeven();
                    }
                    return true;
                }
                movementTicks++;
            } else if (player == 0) {
                g.setFont(new Font("Arial", Font.BOLD, 30));
                g.drawString(value, getX(true) + 5, getY(true) + 35);
            }
            return false;
        }
    }

    private void setSeven() {
        ghostMarbles = new Marble[28];
        Marble currentMarble;
        Marble aheadMarble;
        int iterations;

        for (int i = 0; i < 4; i++) {
            currentMarble = marbles[0][i];
            if (currentMarble.in_play || currentMarble.home) {
                aheadMarble = currentMarble.findMarbleAhead();
                if (aheadMarble == null) {

                    if (currentMarble.getCoolerSpot() + 7 > 67) {
                        iterations = 7 - (currentMarble.getCoolerSpot() - 60);
                    } else {
                        iterations = 7;
                    }

                    iterations = Math.min(iterations, sevenMovesRemaining);

                    for (int j = 0; j < iterations; j++) {
                        ghostMarbles[i * 7 + j] = new Marble(0, "Green");
                        ghostMarbles[i * 7 + j].image = new ImageIcon(getClass().getResource("/images/ghost_marble.png")).getImage();

                        ghostMarbles[i * 7 + j].in_play = currentMarble.in_play;
                        ghostMarbles[i * 7 + j].home = currentMarble.home;

                        ghostMarbles[i * 7 + j].setSpot(currentMarble.getSpot());
                        ghostMarbles[i * 7 + j].addSpot(j + 1);
                    }
                } else {
                    iterations = Math.min(aheadMarble.getCoolerSpot() - currentMarble.getCoolerSpot(), sevenMovesRemaining);

                    for (int j = 0; j < iterations; j++) {
                        ghostMarbles[i * 7 + j] = new Marble(0, "Green");
                        ghostMarbles[i * 7 + j].image = new ImageIcon(getClass().getResource("/images/ghost_marble.png")).getImage();

                        ghostMarbles[i * 7 + j].in_play = currentMarble.in_play;
                        ghostMarbles[i * 7 + j].home = currentMarble.home;

                        ghostMarbles[i * 7 + j].setSpot(currentMarble.getSpot());
                        ghostMarbles[i * 7 + j].addSpot(j + 1);
                    }
                }
            }
        }
    }
    class NonPlayableCard {
        private String value;
        private Image image;
        private int y;
        private int x;
        private int height;
        public NonPlayableCard(String value, String filename, int height) {
            this.value = value;
            image = new ImageIcon(getClass().getResource(filename)).getImage();
            this.height = height;
        }

        private int getX() {
            if (y == 0) {
                return getWidth() / 2 - height * 5 / 14;
            }
            return x;
        }
        private int getY() {
            if (y == 0) {
                return getHeight() / 2 - height / 2;
            }
            return y;
        }
        public void setX(int x) {
            this.x = x;
        }
        public void setY(int y) {
            this.y = y;
        }

        public void draw(Graphics g, ImageObserver observer) {
            if (value.compareTo("") != 0) {
                g.drawImage(image, getX(), getY(), height * 5 / 7, height, observer);

                g.setFont(new Font("Arial", Font.BOLD, 30));
                g.drawString(value, getX() + height * 5 / 28, getY() + height / 3 * 2);
            }
        }
    }
    class Marble {
        private int spot;
        private boolean in_play;
        private Image image;
        private int player;
        private boolean home;
        public Marble(int player, String color) {
            if (color.compareTo("Red") == 0) {
                this.image = new ImageIcon(getClass().getResource("/images/red_marble.png")).getImage();
            } else if (color.compareTo("Blue") == 0) {
                this.image = new ImageIcon(getClass().getResource("/images/blue_marble.png")).getImage();
            } else if (color.compareTo("Green") == 0) {
                this.image = new ImageIcon(getClass().getResource("/images/green_marble.png")).getImage();
            } else if (color.compareTo("Orange") == 0) {
                this.image = new ImageIcon(getClass().getResource("/images/orange_marble.png")).getImage();
            }
            this.player = player;
        }
        public void addSpot(int displacement) {
            int newSpot = displacement + spot;
            if (newSpot > 63) {
                if (newSpot - 64 < 4) {
                    home = true;
                    in_play = false;
                    spot = newSpot % 4;
                }
            } else if (home) {
                if (newSpot < 4) {
                    this.spot = newSpot;
                }

            } else {
                setSpot(newSpot);
            }
        }
        private int getSpot() {
            return (spot + 64 + 16 * player) % 64;
        }
        private int getRawSpot() {
            return (spot + 64) % 64;
        }
        public int getCoolerSpot() {
            if (player != 0) return -1;
            if (home) return spot + 64;
            else if (in_play) return spot;
            return -1;
        }
        public void setSpot(int spot) {
            this.spot = (spot + 64) % 64;
        }
        public boolean collide(int x, int y) {
            Double[] cords;
            if (home) {
                cords = homeMap[player][spot];
            } else if (in_play){
                cords = locationMap[getSpot()];
            } else {
                cords = startMap[player][spot];
            }
            int[] boardInfo = getBoardLocation();
            return (y >= cords[1] * boardInfo[2] + boardInfo[1]) &&
                    (y <= cords[1] * boardInfo[2] + boardInfo[1] + (int) (20 * boardInfo[2] / 457)) &&
                    (x >= cords[0] * boardInfo[2] + boardInfo[0]) &&
                    (x <= cords[0] * boardInfo[2] + boardInfo[0] + (20 * boardInfo[2] / 457));
        }
        public Marble findMarbleAhead() {
            Marble marbleAhead = null;
            Marble currMarble;

            for (int i = 0; i < 4; i++) {
                currMarble = marbles[player][i];
                if (currMarble.getCoolerSpot() > getCoolerSpot()) {
                    if (marbleAhead == null || currMarble.getCoolerSpot() < marbleAhead.getCoolerSpot()) {
                        marbleAhead = currMarble;
                    }
                }
            }

            return marbleAhead;
        }
        private int getSize() {
            return 20 * getBoardLocation()[2] / 457;
        }
        public void draw(Graphics g, ImageObserver observer) {
            Double[] x;
            if (home) {
                x = homeMap[player][spot];
            } else if (in_play){
                x = locationMap[getSpot()];
            }
            else {
                x = startMap[player][spot];
            }
            int[] boardInfo = getBoardLocation();
            g.drawImage(image, (int) (x[0] * boardInfo[2] + boardInfo[0]),
                    (int) (x[1] * boardInfo[2] + boardInfo[1]),
                    20 * boardInfo[2] / 457,
                    20 * boardInfo[2] / 457, observer);
        }
    }
    private Image board;
    private Card[] cards = new Card[20];
    private int[] cardTotals = new int[4];
    private int cardCount;
    private NonPlayableCard pile;
    private NonPlayableCard dealerStack;
    private Marble[][] marbles = new Marble[4][4];
    private Marble[] ghostMarbles = new Marble[28];
    private int sevenMovesRemaining;
    private int playerTurn = 0;
    private int dealer = 3;
    private boolean cardSelect = true;
    private Marble swappingMarble = null;
    public ImagePanel() {
        // Load the image from the given file path
        board = new ImageIcon(getClass().getResource("/images/board16.png")).getImage();  // You can replace this with ImageIO if needed
        setOpaque(false);

        cardValues.put("A", 1);
        cardValues.put("2", 2);
        cardValues.put("3", 3);
        cardValues.put("4", -4);
        cardValues.put("5", 5);
        cardValues.put("6", 6);
        cardValues.put("7", 7);
        cardValues.put("8", 8);
        cardValues.put("9", 9);
        cardValues.put("10", 10);
        cardValues.put("J", 11);
        cardValues.put("Q", 12);
        cardValues.put("K", 13);

        locationMap = new Double[][]{
                {.455, .926},
                {.403, .926}, {.403, .876}, {.403, .826}, {.403, .776}, {.403, .726},
                {.400, .666}, {.375, .621}, {.338, .582}, {.290, .560},
                {.236, .555}, {.184, .555}, {.133, .555}, {.081, .555}, {.031, .555},
                {.031, .505}, {.031, .455},
                {.031, .405}, {.081, .405}, {.133, .405}, {.184, .405}, {.236, .405},
                {.290, .400}, {.338, .378}, {.375, .339}, {.400, .294},
                {.403, .233}, {.403, .183}, {.403, .133}, {.403, .083}, {.403, .033},
                {.454, .033}, {.504, .033},
                {.555, .033}, {.555, .083}, {.555, .133}, {.555, .183}, {.555, .233},
                {.558, .290}, {.583, .339}, {.620, .378}, {.670, .400},
                {.726, .403}, {.776, .403}, {.826, .403}, {.876, .403}, {.926, .403},
                {.926, .455}, {.926, .505},
                {.926, .555}, {.876, .555}, {.826, .555}, {.776, .555}, {.726, .555},
                {.670, .560}, {.620, .582}, {.583, .621}, {.558, .668},
                {.555, .726}, {.555, .776}, {.555, .826}, {.555, .876}, {.555, .926},
                {.505, .926}
        };
        homeMap = new Double[][][] {
                {
                    {.480, .876}, {.480, .826}, {.480, .776}, {.480, .726}
                }, {
                    {.081, .480}, {.133, .480}, {.184, .480}, {.236, .480}
                }, {
                    {.480, .083}, {.480, .133}, {.480, .183}, {.480, .233}
                }, {
                    {.876, .480}, {.826, .480}, {.776, .480}, {.726, .480}
                }
        };
        startMap = new Double[][][]{ {
                        {.764, .788}, {.764, .723}, {.732, .755}, {.795, .755}
                }, {
                        {.212, .795}, {.212, .730}, {.180, .762}, {.244, .762}
                }, {
                        {.212, .168}, {.212, .232}, {.180, .200}, {.244, .200}
                }, {
                        {.748, .168}, {.748, .232}, {.716, .200}, {.780, .200}
        } };

        for (int i = 0; i < 4; i++) {
            marbles[0][i] = new Marble(0, "Green");
            marbles[0][i].setSpot(i);
            marbles[1][i] = new Marble(1, "Blue");
            marbles[1][i].setSpot(i);
            marbles[2][i] = new Marble(2, "Red");
            marbles[2][i].setSpot(i);
            marbles[3][i] = new Marble(3, "Orange");
            marbles[3][i].setSpot(i);
        }
        marbles[1][0].in_play = true;
        marbles[1][0].setSpot(-9);
        marbles[0][3].in_play = true;
        marbles[0][3].setSpot(-2);
        marbles[0][2].in_play = true;
        marbles[0][2].setSpot(0);
    }

    public void drawCards(int n, HashMap<Integer, String[]> playerHands) {
        cards = new Card[20];
        Integer[] players = playerHands.keySet().toArray(new Integer[4]);
        for (Integer i : players) {
            if (i != null) {
                System.out.println(Arrays.toString(playerHands.get(i)));
                dealCards(i, n, playerHands.get(i));
                cardTotals[i] = n;
            }
        }
    }

    public void dealCards(int player, int n, String[] values) {
        Card[] tempCards = new Card[n];
        for (int i = 0; i < n; i++) {
            tempCards[i] = new Card(player, n, values[i]);
        }
        Arrays.sort(tempCards);
        for (int i = 0; i < n; i++) {
            cards[cardCount] = new Card(player, i, tempCards[i].value);
            cardCount++;
        }
        repaint();
    }

    public void removeGhostCard(int player) {

        for (int i = 4; i >= 0; i--) {
            if (cards[player * 5 + i] != null) {
                if (!cards[player * 5 + i].played) {
                    cards[player * 5 + i].played = true;
                    cardTotals[player]--;
                    cardCount--;
                }
                break;
            }
        }
    }

    public void mousePress(int mouseX, int mouseY) {
        if (cardSelect && playerTurn == 0) {
            if (remainingPlays(playerTurn)) {
                selectCard(mouseX, mouseY - 28);
            } else {
                for (int i = 0; i < 5; i++) {
                    if (cards[playerTurn * 5 + i] != null) {
                        cards[playerTurn * 5 + i].played();
                    }
                }
                playerTurn += 1;
            }
        } else if (playerTurn == 0) {
            selectMarble(mouseX, mouseY - 28);
        }
    }

    public void selectMarble(int mouseX, int mouseY) {
        Marble marble;
        if (cardValues.get(pile.value) == 11) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    marble = marbles[i][j];
                    if (marble != null && marble.collide(mouseX, mouseY) && marble.in_play) {
                        if (handleJack(marble, false)) {
                            swappingMarble = null;
                            cardSelect = true;
                            ghostMarbles[0] = null;
                            break;
                        }
                    }
                }
            }
            marble = ghostMarbles[0];
            if (marble != null && marble.collide(mouseX, mouseY)) {
                if (handleJack(marble, true)) {
                    swappingMarble = null;
                    cardSelect = true;
                }
            }
            return;
        }
        if (cardValues.get(pile.value) == 7) {
            for (int i = 0; i < 28; i++) {
                marble = ghostMarbles[i];
                if (marble != null && marble.collide(mouseX, mouseY) && (marble.in_play || marble.home)) {
                    if (handleSeven(i)) {
                        cardSelect = true;
                        for (int j = 0; j < 28; j++) { // There's a way to make this upwards of 6 times more efficient, but lazy
                            ghostMarbles[j] = null;
                        }
                        break;
                    } else {
                        setSeven();
                    }
                    break;
                }
            }
            return;
        }
        for (int i = 0; i < 4; i++) {
            marble = marbles[0][i];
            if (marble != null && marble.collide(mouseX, mouseY) && validPlay(marble, cardValues.get(pile.value))) {
                if (handleCollision(marble)) {
                    /*
                        Might be able to remove if statement here, since we are checking validPlay
                     */

                    cardSelect = true;
                    break;
                }
            }
        }
    }
    public boolean remainingPlays(int player) {
        Card currCard;
        for (int i = 0; i < 5; i++) {
            currCard = cards[player * 5 + i];
            for (int j = 0; j < 4; j++) {
                if (currCard != null && validPlay(marbles[player][j], cardValues.get(currCard.value))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean handleSeven(int index) {
        if (sevenMovesRemaining <= 0) return true;

        int player = index / 7;

        for (int i = 0; i < 4; i++) {
            if (i != player) {
                for (int j = 0; j < 4; j++) {
                    if (marbles[0][player].in_play) {
                        if (marbles[i][j].getSpot() > marbles[0][player].getSpot() &&
                                marbles[i][j].getSpot() <= marbles[0][player].getSpot() + index % 7 + 1) {
                            sendHome(marbles[i][j]);
                        }
                    }
                }
            }
        }

        marbles[0][player].addSpot(index % 7 + 1);
        sevenMovesRemaining -= index % 7 + 1;

        return (sevenMovesRemaining <= 0);
    }

    private boolean handleJack(Marble marble, boolean ghost) {
        if (marble.player == playerTurn && !ghost) {
            swappingMarble = marble;
            if (marble.getSpot() + 11 <= 67) {
                for (int i = 0; i < 4; i++) {
                    if (marbles[0][i].getCoolerSpot() > marble.getCoolerSpot() &&
                        marbles[0][i].getCoolerSpot() <= marble.getCoolerSpot() + 11) {
                        ghost = true;
                    }
                }
                if (!ghost) {
                    ghostMarbles[0] = new Marble(0, "Green");
                    ghostMarbles[0].image = new ImageIcon(getClass().getResource("/images/ghost_marble.png")).getImage();
                    ghostMarbles[0].in_play = true;

                    ghostMarbles[0].setSpot(marble.getSpot());
                    ghostMarbles[0].addSpot(11);
                } else {
                    ghostMarbles[0] = null;
                }

            } else {
                ghostMarbles[0] = null;
            }
            return false;
        } else if (ghost) {
            swappingMarble.addSpot(11);
        } else {
            if (swappingMarble == null) {
                return false;
            }
            int tempSpot = marble.getSpot();
            marble.setSpot((swappingMarble.getSpot() + 16 * (swappingMarble.player - marble.player)) % 64);
            swappingMarble.setSpot(tempSpot);
            swappingMarble = null;
        }
        return true;
    }

    private boolean handleCollision(Marble marble) {
        int cardValue = cardValues.get(pile.value);
        Marble otherMarble;
        if (!marble.home && !marble.in_play) {
            if (Math.abs(cardValue - 7) == 6) {
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        otherMarble = marbles[i][j];
                        if (otherMarble.in_play && !otherMarble.home && otherMarble.getSpot() == 0) {
                            if (marble.player != i) {
                                sendHome(otherMarble);
                            } else {
                                return false;
                            }
                        }
                    }
                }
                marble.setSpot(0);
                marble.home = false;
                marble.in_play = true;
                return true;
            } else {
                return false;
            }
        } else if (marble.in_play) {
            int newSpot = marble.getSpot() + cardValue;
            for (int i = 0; i < 4; i++) {
                otherMarble = marbles[marble.player][i];
                if (otherMarble.in_play) {
                    if ((otherMarble.getSpot() <= newSpot) && (otherMarble.getSpot() > marble.getSpot())) {
                        return false;
                    }
                }
            }
            for (int i = 0; i < 4; i++) {
                if (marble.player != i) {
                    for (int j = 0; j < 4; j++) {
                        otherMarble = marbles[i][j];
                        if (otherMarble.in_play && otherMarble.getSpot() == newSpot) {
                            sendHome(otherMarble);
                            break;
                        }
                    }
                }
            }
            marble.addSpot(cardValue);
        }
        return true;
    }

    /**
     * Returns false whenever illegal
     * @param marble marble being checked
     * @param cardValue value of card being checked for marble
     * @return true if there is a valid play
     */
    private boolean validPlay(Marble marble, int cardValue) {
        int newSpot;
        Marble otherMarble;
        if (cardValue == 7) {
            return validSeven(marble.player);
        }
        if (marble.home) {
            if (marble.getSpot() + Math.abs(cardValue) > 3) { // 4 will always exceed this so no concerns (same with J)
                return false;
            }
            newSpot = marble.getSpot() + cardValue;
            for (int i = 0; i < 4; i++) {
                otherMarble = marbles[marble.player][i];
                if ((otherMarble.home) && (otherMarble.getSpot() <= newSpot)) {
                    return false; // sends when another marble is home and has less value
                }
            }
        } else if (!marble.in_play) {
            if (Math.abs(cardValue - 7) == 6) { // A or K exclusively
                for (int i = 0; i < 4; i++) {
                    otherMarble = marbles[marble.player][i];
                    if (otherMarble.in_play && !otherMarble.home && (otherMarble.getSpot() == 0)) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        } else {
            if (cardValue == 13) {
                for (int i = 0; i < 4; i++) {
                    if (!marbles[marble.player][i].in_play || !marbles[marble.player][i].home) {
                        return false;
                    }
                }
            } else if (cardValue == 11) {
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        if (i != playerTurn) {
                            if (marbles[i][j].in_play) {
                                return true;
                            }
                        }
                    }
                }
            }
            newSpot = marble.getSpot() + cardValue;
            if (newSpot < 64) {
                for (int i = 0; i < 4; i++) {
                    otherMarble = marbles[marble.player][i];
                    if (otherMarble.in_play) {
                        if ((otherMarble.getSpot() <= newSpot) && (otherMarble.getSpot() > marble.getSpot())) {
                            return false;
                        }
                    }
                }
            }
            else if (newSpot > 67) {
                return false;
            } else {
                newSpot = newSpot % 64;
                for (int i = 0; i < 4; i++) {
                    otherMarble = marbles[marble.player][i];
                    if ((otherMarble.home) && (otherMarble.getSpot() <= newSpot)) {
                        return false; // sends when another marble is home and has less value
                    }
                }
            }
        }

        return true;
    }

    private boolean validSeven(int player) {
        int[] spots = new int[]{0, 0, 0, 0, 0, 0, 0};
        int marbleCount = 0;
        Marble marble;
        for (int i = 0; i < 4; i++) {
            marble = marbles[player][i];
            int newSpot;
            if (marble != null && (marble.in_play || marble.home)) {
                if (marble.home) {
                    newSpot = marble.getSpot() + 64;
                } else {
                    newSpot = marble.getSpot();
                }
                if (newSpot < 61) {
                    return true;
                }
                spots[67 - newSpot] = 1;
                marbleCount += 1;
            }
        }

        int count = 0;

        for (int i = 6; i >= 0; i--) {
            if (spots[0] == 0) {
                count += marbleCount;
            } else {
                marbleCount -= 1;
                count += marbleCount;
            }

            if (count >= 7) {
                return true;
            }
        }

        return false;
    }

    private void sendHome(Marble marble) {
        boolean[] takenSpots = new boolean[4];
        Marble otherMarble;
        for (int i = 0; i < 4; i++) {
            otherMarble = marbles[marble.player][i];
            if (!otherMarble.in_play && !otherMarble.home) {
                takenSpots[otherMarble.getRawSpot()] = true;
            }
        }
        for (int i = 0; i < 4; i++) {
            if (!takenSpots[i]) {
                marble.setSpot(i);
            }
        }
        marble.in_play = false;
    }

    public void selectCard(int mouseX, int mouseY) {
        for (int i = 0; i < 5; i++) {
            if (cards[i] != null) {
                if (cards[i].played) {
                    return;
                }
            }
        }
        int index = -1;
        for (int i = 4; i >= 0; i--) {
            if (cards[i] != null) {
                if ((mouseX >= cards[i].getX(true)) && (mouseY >= cards[i].getY(true))) {
                    if (cards[i].selected) {
                        for (int j = 0; j < 4; j++) {
                            if (validPlay(marbles[playerTurn][j], cardValues.get(cards[i].value))) {
                                removeCard(mouseX, mouseY);
                                /*
                                    kinda funky, we already have the card to be selected
                                    want to check later but too lazy since it works
                                 */
                                break;
                            }
                        }
                        break;
                    }
                    cards[i].selected = true;
                    index = i;
                    break;
                } else {
                    cards[i].selected = false;
                }
            }
        }
        for (int i = 0; i < index; i++) {
            if (cards[i] != null) {
                cards[i].selected = false;
            }
        }
    }

    public void removeCard(int mouseX, int mouseY) {

        int index = 4;
        for (int i = 4; i >= 0; i--) {
            if (cards[i] != null) {
                if (mouseX >= cards[i].getX(true)) {
                    cards[i].played();
                    cardTotals[0]--;
                    cardCount--;
                    index = i;
                    break;
                }
            }
        }
        for (int i = 4; i > index; i--) {
            if (cards[i] != null) {
                cards[i].index--;
            }
        }
    }
    private int[] getBoardLocation() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int imageHeight = Math.min(panelWidth, panelHeight) * 8 / 10;

        int x = (panelWidth - imageHeight) / 2;
        int y = (panelHeight - imageHeight) / 2;
        return new int[] {x, y, imageHeight};
    }
    private void move(int value, Graphics g, ImageObserver observer) {
        Marble marble;
        if (Math.abs(value - 7) == 6) {
            for (int i = 0; i < 4; i++) {
                marble = marbles[playerTurn][i];
                if (!marble.home && !marble.in_play)  {
                    marble.in_play = true;
                    marble.spot = 0;
                    marble.draw(g, observer);
                    break;
                }
            }
        } else {
            for (int i = 0; i < 4; i++) {
                marble = marbles[playerTurn][i];
                if (marble.in_play)  {
                    marble.addSpot(value);
                    break;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int imageHeight = Math.min(panelWidth, panelHeight) * 8 / 10;

        int x = (panelWidth - imageHeight) / 2;
        int y = (panelHeight - imageHeight) / 2;

        g.setColor(Color.decode("#4F9153"));
        g.fillRect(0, 0, panelWidth, panelHeight);

        g.drawImage(board, x, y, imageHeight, imageHeight, this);

        for (int i = 0; i < 28; i++) {
            if (ghostMarbles[i] != null) {
                ghostMarbles[i].draw(g, this);
            }
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (marbles[i][j] != null) {
                    marbles[i][j].draw(g, this);
                }
            }
        }

        for (int i = 0; i < 20; i++) {
            if (cards[i] != null) {
                if (cards[i].draw(g, this)) {
                    cards[i] = null;
                }
            }
        }

        if (pile == null) {
            pile = new NonPlayableCard("", "/images/blankCard.png", 70);
        }
        pile.draw(g, this);

        if (dealerStack == null) {
            dealerStack = new NonPlayableCard("8", "/images/cardBack.png", 70);
            if (dealer == 3) {
                dealerStack.setX(getWidth() - dealerStack.height * 5 / 7 - 1);
                dealerStack.setY(1);
            }
        }
        g.setColor(Color.WHITE);
        dealerStack.draw(g, this);
    }
}