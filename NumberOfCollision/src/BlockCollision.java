import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class BlockCollision extends JPanel implements ActionListener {
    private Timer timer;
    private Block block1, block2;
    private int collisionCount;
    private JTextField massInput1, massInput2, velocityInput;
    private JButton startButton, pauseButton;
    private boolean isPaused = false;

    private static final int BLOCK_SIZE = 50;

    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 300;

    private static final double TIME_STEP = 0.05;

    public BlockCollision() {
        block1 = new Block(100, 0, 1000, BLOCK_SIZE);
        block2 = new Block(500, 0, 1000, BLOCK_SIZE);
        collisionCount = 0;

        timer = new Timer(2, this);

        massInput1 = new JTextField("1000", 5);
        massInput2 = new JTextField("1000", 5);
        velocityInput = new JTextField("-10", 5);

        startButton = new JButton("OK");
        startButton.addActionListener(e -> startSimulation());

        pauseButton = new JButton("Pause and reset stats");
        pauseButton.addActionListener(e -> pauseSimulation());

        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Mass of Block 1 (grams):"));
        controlPanel.add(massInput1);
        controlPanel.add(new JLabel("Mass of Block 2 (grams):"));
        controlPanel.add(massInput2);
        controlPanel.add(new JLabel("Velocity of Block 2 (m/s):"));
        controlPanel.add(velocityInput);
        controlPanel.add(startButton);
        controlPanel.add(pauseButton);

        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void startSimulation() {
        try {
            double mass1 = Double.parseDouble(massInput1.getText());
            double mass2 = Double.parseDouble(massInput2.getText());
            int velocity2 = Integer.parseInt(velocityInput.getText());

            block1.mass = mass1;
            block2.mass = mass2;
            block2.velocity = velocity2;

            // Reset number of collision
            block1.positionX = 100;
            block2.positionX = 500;
            collisionCount = 0;

            if (isPaused) {
                timer.restart();
                isPaused = false;
            } else {
                timer.start();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers!");
        }
    }

    private void pauseSimulation() {
        if (!isPaused) {
            block1.velocity = 0;
            timer.stop();
            isPaused = true;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // move
        block1.move(TIME_STEP);
        block2.move(TIME_STEP);

        // check collision
        if (checkCollision(block1, block2)) {
            handleCollision(block1, block2);
            collisionCount++;
            playCollisionSound();
        }

        // check wall collision
        if (block1.positionX <= 50) {
            block1.reverseVelocity();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.BLACK);

       // horizon
        g.setColor(Color.WHITE);
        g.drawLine(0, WINDOW_HEIGHT / 2, WINDOW_WIDTH, WINDOW_HEIGHT / 2);

        // vertical
        g.drawLine(50, 0, 50, WINDOW_HEIGHT / 2);

       // block 1
        g.setColor(Color.RED);
        g.fillRect((int) block1.positionX, WINDOW_HEIGHT / 2 - BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);

       // block 2
        g.setColor(Color.BLUE);
        g.fillRect((int) block2.positionX, WINDOW_HEIGHT / 2 - BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);

        // display mass of two blocks
        g.setColor(Color.WHITE);
        g.drawString(String.format("%.2f kg", block1.mass / 1000.0), (int) block1.positionX, WINDOW_HEIGHT / 2 - BLOCK_SIZE - 10);
        g.drawString(String.format("%.2f kg", block2.mass / 1000.0), (int) block2.positionX, WINDOW_HEIGHT / 2 - BLOCK_SIZE - 10);

        // display collisions
        g.drawString("Collision(s): " + collisionCount, 600, 20);
    }

    // check collision
    public boolean checkCollision(Block b1, Block b2) {
        return b1.positionX + b1.size >= b2.positionX;
    }

    public void handleCollision(Block b1, Block b2) {
        double newV1 = ((b1.mass - b2.mass) * b1.velocity + 2 * b2.mass * b2.velocity) / (b1.mass + b2.mass);
        double newV2 = ((b2.mass - b1.mass) * b2.velocity + 2 * b1.mass * b1.velocity) / (b1.mass + b2.mass);

        b1.velocity = newV1;
        b2.velocity = newV2;
    }

    // sound
    public void playCollisionSound() {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File("D:/NumberOfCollision/resources/collision.wav")));
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Block Collision Simulation");
        BlockCollision simulation = new BlockCollision();
        frame.add(simulation);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
