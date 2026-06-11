package View;

import Model.CalendarModel;
import Model.Event;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Runs a separated thread and, from time to time, checks if any event is getting close to it's
 * reminder time. When it comes, shows an warning in the screen. Each reminder just appears once.
 */
public class ReminderService implements Runnable {
    private static final long INTERVALO_MS = 30_000; // Checks every 30 seconds

    private final CalendarModel model;
    private final Set<String> alreadyWarned = new HashSet<>();
    private Thread thread;

    public ReminderService(CalendarModel model) {
        this.model = model;
    }

    public void start() {
        thread = new Thread(this, "reminder-service");
        thread.setDaemon(true); // does not prevent the program from closing
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            checkReminders();
            try {
                Thread.sleep(INTERVALO_MS);
            } catch (InterruptedException ex) {
                return;
            }
        }
    }

    private void checkReminders() {
        LocalDateTime now = LocalDateTime.now();

        for (Event e : model.getAllEvents()) {
            long lead = e.getReminderLeadMinutes();
            if (lead <= 0) {
                continue;
            }
            // Looks the ocurrences from today and tomorrow
            for (int i = 0; i <= 1; i++) {
                LocalDate dia = now.toLocalDate().plusDays(i);
                if (!e.occursOn(dia)) {
                    continue;
                }
                LocalDateTime begin = e.occurrenceStart(dia);
                LocalDateTime reminder = begin.minusMinutes(lead);

                // passed the reminder time but the event hasn't started yet
                if (!now.isBefore(reminder) && now.isBefore(begin)) {
                    String key = e.getTitle() + "@" + begin;
                    if (alreadyWarned.add(key)) {
                        showWarning(e, begin);
                    }
                }
            }
        }
    }

    private void showWarning(Event e, LocalDateTime inicio) {
        String mensagem = "Reminder: \"" + e.getTitle() + "\"\n"
            + "Starts at " + e.getTime()
            + " (" + e.getCategory() + ")";
        // Shows the reminder in the interface thread
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(null, mensagem, "Lembrete de evento",
                JOptionPane.INFORMATION_MESSAGE));
    }
}
