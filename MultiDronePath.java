import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MultiDronePath extends JFrame {
    private static final int GRID_SIZE = 100;
    private static final int PANEL_SIZE = 600;
    private static final int PANEL_MARGIN = 10;
    private static final int DRONE_SIZE = 10;
    private static final int ANIMATION_DELAY_MS = 200;

    private List<DronePanel> dronePanels;

    public MultiDronePath(int numDrones) {
        setTitle("Multi-Drone Path");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2, 2, PANEL_MARGIN, PANEL_MARGIN));
        dronePanels = new ArrayList<>();
        for (int i = 1; i <= numDrones; i++) {
            String droneName = "Drone " + i;
            Point startPoint = getValidInputPoint(droneName + " start");
            Point endPoint = getValidInputPoint(droneName + " end");
            DronePanel dronePanel = new DronePanel(droneName, startPoint, endPoint);
            mainPanel.add(dronePanel);
            dronePanels.add(dronePanel);
        }

        add(mainPanel, BorderLayout.CENTER);

        setSize(PANEL_SIZE + PANEL_MARGIN * 2, PANEL_SIZE + PANEL_MARGIN * 2);
        setLocationRelativeTo(null);
        setVisible(true);

        startAnimation();
    }

    private Point getValidInputPoint(String prompt) {
        int x = -1;
        int y = -1;
        while (x < 0 || x >= GRID_SIZE || y < 0 || y >= GRID_SIZE) {
            String input = JOptionPane.showInputDialog(prompt + " (x y):");
            String[] parts = input.split(" ");
            x = Integer.parseInt(parts[0]);
            y = Integer.parseInt(parts[1]);
            if (x < 0 || x >= GRID_SIZE || y < 0 || y >= GRID_SIZE) {
                JOptionPane.showMessageDialog(this, "Invalid coordinates, please try again.");
            }
        }
        return new Point(x, y);
    }

    private void startAnimation() {
        for (DronePanel dronePanel : dronePanels) {
            new Thread(() -> {
                while (!dronePanel.isAtEnd()) {
                    dronePanel.move();
                    checkCollision(dronePanel);
                    dronePanel.repaint();
                    try {
                        Thread.sleep(ANIMATION_DELAY_MS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void checkCollision(DronePanel currentDrone) {
        for (DronePanel dronePanel : dronePanels) {
            if (dronePanel != currentDrone) {
                if (dronePanel.currentPoint.equals(currentDrone.currentPoint)) {
                    int dx = (int) (Math.random() * 2) == 0 ? -1 : 1;
                    int dy = (int) (Math.random() * 2) == 0 ? -1 : 1;
                    currentDrone.currentPoint.x += dx;
                    currentDrone.currentPoint.y += dy;
                }
            }
        }
    }

    private class DronePanel extends JPanel {
        private String droneName;
        private Point startPoint;
        private Point endPoint;
        private Point currentPoint;

        public DronePanel(String droneName, Point startPoint, Point endPoint) {
            this.droneName = droneName;
            this.startPoint = startPoint;
            this.endPoint = endPoint;
            this.currentPoint = startPoint;
        }

        public boolean isAtEnd() {
            return currentPoint.equals(endPoint);
        }

        public void move() {
            int dx = 0;
            int dy = 0;
            if (currentPoint.x < endPoint.x) {
                dx = 1;
            } else if (currentPoint.x > endPoint.x) {
                dx = -1;
            }
            if (currentPoint.y < endPoint.y) {
                dy = 1;
            } else if (currentPoint.y > endPoint.y) {
                dy = -1;
            }
            currentPoint.x += dx;
            currentPoint.y += dy;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.fillRect(currentPoint.x * (PANEL_SIZE / GRID_SIZE), currentPoint.y * (PANEL_SIZE / GRID_SIZE), DRONE_SIZE, DRONE_SIZE);
            g.setColor(Color.RED);
            g.fillRect(startPoint.x * (PANEL_SIZE / GRID_SIZE) + DRONE_SIZE / 2 - 1, startPoint.y * (PANEL_SIZE / GRID_SIZE) + DRONE_SIZE / 2 - 1, 2, 2);
            g.fillRect(endPoint.x * (PANEL_SIZE / GRID_SIZE) + DRONE_SIZE / 2 - 1, endPoint.y * (PANEL_SIZE / GRID_SIZE) + DRONE_SIZE / 2 - 1, 2, 2);
            g.setColor(Color.WHITE);
            g.drawString(droneName, currentPoint.x * (PANEL_SIZE / GRID_SIZE) + DRONE_SIZE, currentPoint.y * (PANEL_SIZE / GRID_SIZE) + DRONE_SIZE);
        }
    }

    public static void main(String[] args) {
        int numDrones = Integer.parseInt(JOptionPane.showInputDialog("Enter number of drones:"));
        new MultiDronePath(numDrones);
    }
}