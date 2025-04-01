/*
import java.awt.*;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.ImageIcon;

public class Card extends JPanel {
    private int value;
    private boolean front;
    private Image image;
    private final int player;
    private int origX = 0;
    private int origY = 0;

    public Card(int player) {

        if (player == 2) {
            image = new ImageIcon(getClass().getResource("cardBack.png")).getImage();
            origX = 600;
            origY = 300;
        } else if (player > 0) {
            image = new ImageIcon(getClass().getResource("sideCard.png")).getImage();
        } else {
            image = new ImageIcon(getClass().getResource("blackCard.png")).getImage();
        }

        setOpaque(false);
        this.player = player;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int height;
        int width;

        if (player % 2 == 0) {
            height = Math.min(getWidth(), getHeight()) / 2;
            width = height * 5 / 7;
        } else {
            width = Math.min(getWidth(), getHeight()) / 2;
            height = width * 5 / 7;
        }

        g.drawImage(image, origX, origY, width, height, this);
    }
}
*/