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
 * Runs on a separate daemon thread and periodically checks whether any event is
 * approaching its reminder time. When the time arrives, a message is shown on the
 * screen. Each reminder is shown only once.
 */
public class ReminderService implements Runnable {
    /** How often the reminders are checked, in milliseconds. */
    private static final long INTERVAL_MS = 30_000;

    private final CalendarModel model;
    private final Set<String> alreadyWarned = new HashSet<>();
    private Thread thread;

    /**
     * Creates the service for the given model.
     *
     * @param model the calendar model whose events are watched
     */
    public ReminderService(CalendarModel model) {
        this.model = model;
    }

    /** Starts the background thread. */
    public void start() {
        thread = new Thread(this, "reminder-service");
        thread.setDaemon(true); // does not keep the program alive
        thread.start();
    }

    /** Main loop: check the reminders, then sleep. */
    @Override
    public void run() {
        while (true) {
            checkReminders();
            try {
                Thread.sleep(INTERVAL_MS);
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
            // Look at the occurrences for today and tomorrow.
            for (int i = 0; i <= 1; i++) {
                LocalDate day = now.toLocalDate().plusDays(i);
                if (!e.occursOn(day)) {
                    continue;
                }
                LocalDateTime begin = e.occurrenceStart(day);
                LocalDateTime reminder = begin.minusMinutes(lead);

                // Past the reminder time but the event has not started yet.
                if (!now.isBefore(reminder) && now.isBefore(begin)) {
                    String key = e.getTitle() + "@" + begin;
                    if (alreadyWarned.add(key)) {
                        showWarning(e);
                    }
                }
            }
        }
    }

    private void showWarning(Event e) {
        String message = "Reminder: \"" + e.getTitle() + "\"\n"
            + "Starts at " + e.getTime()
            + " (" + e.getCategory() + ")";
        // Show the reminder on the interface thread.
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(null, message, "Event reminder",
                JOptionPane.INFORMATION_MESSAGE));
    }
}
