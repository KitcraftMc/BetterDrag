import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.AWTException;
import java.util.ArrayList;
import java.util.List;

public class BetterDrag {
    private static int singleClickMultiplier = 1; // Default clicks per single click
    private static int multiClickMultiplier = 1; // Default activations for multi-clicker
    private static int clicksPerSpot = 1; // Default clicks per spot in multi-clicker
    private static boolean ctrlPressed = false; // Tracks if CTRL is pressed
    private static final List<Point> clickPoints = new ArrayList<>(); // Stores points for multi-clicker

    public static void main(String[] args) {
        JFrame frame = new JFrame("BetterDrag");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new FlowLayout());

        JMenuBar menuBar = new JMenuBar();

        // Single Clicker Menu
        JMenu singleClickMenu = new JMenu("Single Clicker");
        JMenuItem configureSingleClick = new JMenuItem("Configure Single Click");
        singleClickMenu.add(configureSingleClick);

        // Separator between menus
        menuBar.add(singleClickMenu);
        menuBar.add(new JSeparator(JSeparator.VERTICAL));

        // Multi-Clicker Menu
        JMenu multiClickMenu = new JMenu("Multi-Clicker");
        JMenuItem configureMultiClick = new JMenuItem("Configure Multi-Clicker");
        JMenuItem clearPoints = new JMenuItem("Clear Points");
        multiClickMenu.add(configureMultiClick);
        multiClickMenu.add(clearPoints);

        menuBar.add(multiClickMenu);
        frame.setJMenuBar(menuBar);

        // Key listener for CTRL key detection
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            ctrlPressed = e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_CONTROL;
            return false;
        });

        // Mouse listener for point selection and click handling
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (event.getID() == MouseEvent.MOUSE_PRESSED) {
                MouseEvent mouseEvent = (MouseEvent) event;

                if (ctrlPressed && mouseEvent.getButton() == MouseEvent.BUTTON1) {
                    // Add new click point for multi-clicker
                    Point newPoint = MouseInfo.getPointerInfo().getLocation();
                    clickPoints.add(newPoint);
                    System.out.println("Added point: " + newPoint);
                } else if (!ctrlPressed && mouseEvent.getButton() == MouseEvent.BUTTON1) {
                    // Trigger actions based on configuration
                    new Thread(() -> simulateSingleClicks()).start();
                    new Thread(() -> simulateMultiClicks()).start();
                }
            }
        }, AWTEvent.MOUSE_EVENT_MASK);

        configureSingleClick.addActionListener(e -> showSingleClickConfigurationDialog(frame));
        configureMultiClick.addActionListener(e -> showMultiClickConfigurationDialog(frame));
        clearPoints.addActionListener(e -> {
            clickPoints.clear();
            JOptionPane.showMessageDialog(frame, "All points cleared!");
        });

        frame.setVisible(true);
    }

    private static void showSingleClickConfigurationDialog(JFrame parent) {
        JDialog dialog = new JDialog(parent, "Single Click Configuration", true);
        dialog.setSize(300, 150);
        dialog.setLayout(new FlowLayout());

        JLabel label = new JLabel("Clicks per single left-click:");
        JSpinner multiplierSpinner = new JSpinner(new SpinnerNumberModel(singleClickMultiplier, 1, 1000, 1));

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            singleClickMultiplier = (Integer) multiplierSpinner.getValue();
            JOptionPane.showMessageDialog(dialog,
                "Configuration saved! Single left-click will now simulate " + singleClickMultiplier + " clicks.");
            dialog.dispose();
        });

        dialog.add(label);
        dialog.add(multiplierSpinner);
        dialog.add(saveButton);

        dialog.setVisible(true);
    }

    private static void showMultiClickConfigurationDialog(JFrame parent) {
        JDialog dialog = new JDialog(parent, "Multi-Clicker Configuration", true);
        dialog.setSize(350, 200);
        dialog.setLayout(new GridLayout(4, 2));

        JSpinner activationsSpinner = new JSpinner(new SpinnerNumberModel(multiClickMultiplier, 1, 1000, 1));
        JSpinner perSpotSpinner = new JSpinner(new SpinnerNumberModel(clicksPerSpot, 1, 1000, 1));

        dialog.add(new JLabel("Activations per trigger:"));
        dialog.add(activationsSpinner);
        dialog.add(new JLabel("Clicks per spot:"));
        dialog.add(perSpotSpinner);

        JButton saveButton = new JButton("Save");
        
		saveButton
