package Main;

import Controller.*;
import Model.*;
import View.*;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        CalendarModel model = new CalendarModel();
        EventStorage storage = new EventStorage("events.txt");
        Controller controller = new Controller(model, storage);

        // Loads the saved events before opening the window
        try {
            controller.loadFromDisk();
        } catch (StorageException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(),
                "Erro ao carregar eventos", JOptionPane.ERROR_MESSAGE);
        }

        SwingUtilities.invokeLater(() -> {
            CalendarView view = new CalendarView(controller);
            view.setVisible(true);
            new ReminderService(model).start();
        });
    }
}
