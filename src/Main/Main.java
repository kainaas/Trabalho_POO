package Main;

import Model.*;
import Controller.*;
import View.*;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        CalendarModel model = new CalendarModel();
        EventStorage storage = new EventStorage("events.txt");
        Controller controller = new Controller(model, storage);

        // carrega os eventos salvos antes de abrir a janela
        try {
            controller.loadFromDisk();
        } catch (StorageException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(),
                "Erro ao carregar eventos", JOptionPane.ERROR_MESSAGE);
        }

        SwingUtilities.invokeLater(() -> {
            CalendarView view = new CalendarView(controller);
            view.setVisible(true);
            // thread que fica de olho nos lembretes enquanto o programa roda
            new ReminderService(model).start();
        });
    }
}
