package graphic;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Random;

public class RotatingLines extends JPanel {
    private static final int NUM_LINES = 50;
    private static final int RADIUS = 100;
    private static final int LINE_LENGTH = 80;
    private static final int ANIMATION_DURATION = 100; // Kürzere Dauer für schnellere Drehung

    private double rotationAngle = 0.0;
    private int progress = 0;
    private Timer timer;
    private Random random = new Random();

    public RotatingLines() {
        setPreferredSize(new Dimension(400, 400));
        setBackground(Color.WHITE);

        timer = new Timer(ANIMATION_DURATION, e -> {
            rotationAngle += Math.toRadians(360.0 / NUM_LINES);
            progress += 3; // Ändern Sie diese Schrittweite, um die Geschwindigkeit des Fortschritts zu steuern
            if (progress >= 100) {
                progress = 100;
                timer.stop();
            }
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        for (int i = 0; i < NUM_LINES; i++) {
            double angle = 2 * Math.PI / NUM_LINES * i;
            int startX = (int) (centerX + RADIUS * Math.cos(angle + rotationAngle));
            int startY = (int) (centerY + RADIUS * Math.sin(angle + rotationAngle));
            int endX = (int) (startX + LINE_LENGTH * Math.cos(angle + rotationAngle));
            int endY = (int) (startY + LINE_LENGTH * Math.sin(angle + rotationAngle));

            g2d.setColor(getRandomColor());
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(startX, startY, endX, endY);
        }

        int barWidth = 200;
        int barHeight = 20;
        int barX = (getWidth() - barWidth) / 2;
        int barY = (getHeight() - barHeight) / 2;

        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(barX, barY, barWidth, barHeight);

        if (progress > 0) {
            g2d.setColor(Color.BLUE);
            int progressWidth = (int) (barWidth * (progress / 100.0));
            g2d.fillRect(barX, barY, progressWidth, barHeight);

            String progressText = progress + "%";
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(progressText);
            int textHeight = fm.getHeight();
            int textX = barX + (barWidth - textWidth) / 2;
            int textY = barY + (barHeight - textHeight) / 2 + fm.getAscent();
            g2d.setColor(Color.WHITE);
            g2d.drawString(progressText, textX, textY);
        }

        g2d.dispose();
    }

    private Color getRandomColor() {
        float red = random.nextFloat();
        float green = random.nextFloat();
        float blue = random.nextFloat();
        float yellow = random.nextFloat();

        return new Color(red, green, blue, yellow);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Rotating Lines");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(new RotatingLines());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
