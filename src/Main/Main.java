package Main;

import Controller.Controller;
import Model.CalendarModel;
import Model.EventStorage;
import Model.StorageException;
import View.CalendarView;
import View.ReminderService;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Application entry point. Wires the model, the storage and the controller
 * together, loads the saved events and opens the main window.
 */
public class Main {
    /**
     * Starts the Java Event Planner.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        CalendarModel model = new CalendarModel();
        EventStorage storage = new EventStorage("events.txt");
        Controller controller = new Controller(model, storage);

        // Load the saved events before opening the window.
        try {
            controller.loadFromDisk();
        } catch (StorageException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(),
                "Error while loading events", JOptionPane.ERROR_MESSAGE);
        }

        SwingUtilities.invokeLater(() -> {
            CalendarView view = new CalendarView(controller);
            view.setVisible(true);
            new ReminderService(model).start();
        });
    }
}
