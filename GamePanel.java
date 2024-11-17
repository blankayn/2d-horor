import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable {

     int originalTileSize = 16; 
     int scale = 3;
    public int tileSize = originalTileSize * scale; 
    int maxScreenCol = 16;
    int maxScreenRow = 12;
    int screenWidth = tileSize * maxScreenCol; 
        int screenHeight = tileSize * maxScreenRow; 


    int FPS = 60;

    // Game components
    KeyHandler keyH = new KeyHandler();
    Player player = new Player(this, keyH);
    Thread gameThread;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.gray);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void startGameThread() {
        gameThread = new Thread(this); // Create a new thread
        gameThread.start();            // Start the thread
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS; // Nanoseconds per frame
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            update();
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000; // Convert to milliseconds

                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        player.update();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        player.draw(g2);

        g2.dispose();
    }

    public class KeyHandler implements KeyListener {

        public boolean upPressed, downPressed, leftPressed, rightPressed;

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int code = e.getKeyCode();

            if (code == KeyEvent.VK_W) {
                upPressed = true;
            }
            if (code == KeyEvent.VK_S) {
                downPressed = true;
            }
            if (code == KeyEvent.VK_A) {
                leftPressed = true;
            }
            if (code == KeyEvent.VK_D) {
                rightPressed = true;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int code = e.getKeyCode();

            if (code == KeyEvent.VK_W) {
                upPressed = false;
            }
            if (code == KeyEvent.VK_S) {
                downPressed = false;
            }
            if (code == KeyEvent.VK_A) {
                leftPressed = false;
            }
            if (code == KeyEvent.VK_D) {
                rightPressed = false;
            }
        }
    }

    public class Player {

        GamePanel gp;
        KeyHandler keyH;

        // Player position and speed
        int x, y, speed;
        String direction;

        // Images for animation
        BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;

        public Player(GamePanel gp, KeyHandler keyH) {
            this.gp = gp;
            this.keyH = keyH;

            setDefaultValues();
            getPlayerImage();
        }

        public void setDefaultValues() {
            x = 100;
            y = 100;
            speed = 4;
            direction = "down";
        }

        public void getPlayerImage() {
            try {
                up1 = ImageIO.read(getClass().getResourceAsStream("/src/sprite/up.png"));
                up2 = ImageIO.read(getClass().getResourceAsStream("/src/sprite/up1.png"));
                down1 = ImageIO.read(getClass().getResourceAsStream("/src/sprite/down.png"));
                down2 = ImageIO.read(getClass().getResourceAsStream("/src/sprite/down1.png"));
                left1 = ImageIO.read(getClass().getResourceAsStream("/src/sprite/left.png"));
                left2 = ImageIO.read(getClass().getResourceAsStream("/src/sprite/left1.png"));
                right1 = ImageIO.read(getClass().getResourceAsStream("/src/sprite/right.png"));
                right2 = ImageIO.read(getClass().getResourceAsStream("/src/sprite/right1.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void update() {
            if (keyH.upPressed) {
                direction = "up";
                y -= speed;
            } else if (keyH.downPressed) {
                direction = "down";
                y += speed;
            } else if (keyH.leftPressed) {
                direction = "left";
                x -= speed;
            } else if (keyH.rightPressed) {
                direction = "right";
                x += speed;
            }
        }

        public void draw(Graphics2D g2) {
            BufferedImage image = null;

            switch (direction) {
                case "up":
                    image = up1;
                    break;
                case "down":
                    image = down1;
                    break;
                case "left":
                    image = left1;
                    break;
                case "right":
                    image = right1;
                    break;
            }

            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }
    }
}
