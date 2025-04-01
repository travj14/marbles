import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.awt.event.*;
import java.util.HashMap;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class bucky extends JFrame {

    private int width = 800;
    private int height = 600;


    /**
     * An integer is only in the list of keys if it is a valid player
     * Other players will not be included in the hashmap
     */
    private HashMap<Integer, String[]> playerCards = new HashMap<>();
    Cards cardPool = new Cards();

    public bucky() {
        setTitle("Windowed Game"); // Set window title

        playerCards.put(1, new String[5]);
        playerCards.put(2, new String[5]);
        playerCards.put(3, new String[5]);
        playerCards.put(0, new String[5]);

        deal(5);

        playerCards.put(0, new String[] {"A", "7", "A", "8", "J"});

        ImagePanel panel = new ImagePanel();
        add(panel);

        setSize(width, height); // Set preferred size
        setLocationRelativeTo(null); // Center the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close properly
        setResizable(true); // Prevent resizing

        // Add KeyListener to detect ESC key to exit
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.out.println("Escape key pressed! Exiting...");
                    dispose(); // Close window
                    System.exit(0); // Terminate program
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    panel.removeGhostCard(1);
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    panel.removeGhostCard(2);
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    panel.removeGhostCard(3);
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                panel.mousePress(e.getX(), e.getY());
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                width = getWidth();
                height = getHeight();
            }
        });

        Timer timer = new Timer(17, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint(); // Redraw the screen
            }
        });
        timer.start();

        setFocusable(true); // Ensure key events are captured
        setVisible(true); // Show the window

        panel.drawCards(5, playerCards);
    }

    private void deal(int n) {
        Integer[] keys = playerCards.keySet().toArray(new Integer[4]);
        for (int i = 0; i < 4; i++) {
            playerCards.put(keys[i], cardPool.drawCards(n));
        }
    }

    public static void main(String[] args) {
        new bucky();

        // DisplayMode dm = new DisplayMode(800, 600, 16, DisplayMode.REFRESH_RATE_UNKNOWN);
        // bucky b = new bucky();
        // b.run(dm);

        JFrame frame = new JFrame("Two Images Example");
    }

    public void run(DisplayMode dm) {
        setBackground(Color.PINK);
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.PLAIN, 24));

        Screen s = new Screen();
        try {
            s.setFullScreen(dm, this);
            try {
                Thread.sleep(5000);
            } catch (Exception ex) {}
        } finally {
            s.restoreScreen();
        }
    }
}
