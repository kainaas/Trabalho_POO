package View;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import Controller.Controller;
import Model.Attendee;
import Model.CalendarModel;
import Model.Event;
import Model.StorageException;

/**
 * Side panel that shows the events of the selected day. It displays the details
 * of each event and offers buttons to create, edit and delete. When an event
 * recurs, it asks whether the action applies to that single day or to the whole
 * series.
 */
public class DayEventsPanel extends JPanel {
    private static final int SCOPE_THIS = 0;
    private static final int SCOPE_ALL = 1;
    private static final Locale EN = Locale.ENGLISH;

    private final JFrame owner;
    private final Controller controller;
    private final CalendarModel model;

    private final JLabel dateLabel;
    private final DefaultListModel<Event> listModel;
    private final JList<Event> eventJList;
    private final JTextArea details;
    private JButton newButton, editButton, deleteButton;
    private JPanel center, panel;

    /**
     * Builds the side panel.
     *
     * @param owner      the main window (parent of the dialogs)
     * @param controller the controller used for create/edit/delete
     */
    public DayEventsPanel(JFrame owner, Controller controller) {
        this.owner = owner;
        this.controller = controller;
        this.model = controller.getModel();

        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        dateLabel = new JLabel("", SwingConstants.CENTER);
        dateLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        listModel = new DefaultListModel<>();
        eventJList = new JList<>(listModel);
        eventJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventJList.setCellRenderer(new EventRenderer());
        eventJList.addListSelectionListener(e -> showDetails());

        details = new JTextArea(8, 22);
        details.setEditable(false);
        details.setLineWrap(true);
        details.setWrapStyleWord(true);

        JScrollPane listScroll = new JScrollPane(eventJList);
        JScrollPane detailScroll = new JScrollPane(details);

        center = new JPanel(new GridLayout(2, 1, 0, 8));
        center.add(listScroll);
        center.add(detailScroll);

        add(dateLabel, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        refresh();
    }

    private JPanel buildButtons() {
        newButton = new JButton("New");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");

        newButton.addActionListener(e -> openCreate());
        editButton.addActionListener(e -> openEdit());
        deleteButton.addActionListener(e -> doDelete());

        panel = new JPanel(new GridLayout(1, 3, 6, 0));
        panel.add(newButton);
        panel.add(editButton);
        panel.add(deleteButton);
        return panel;
    }

    /** Reloads the day's events and reapplies the theme. */
    public void refresh() {
        ThemeColors mc = ThemeColors.of(model.getDarkMode());
        setBackground(mc.background);
        center.setBackground(mc.background);
        panel.setBackground(mc.background);
        dateLabel.setForeground(mc.text);
        eventJList.setBackground(mc.panel);
        eventJList.setForeground(mc.text);
        details.setBackground(mc.panel);
        details.setForeground(mc.text);
        details.setCaretColor(mc.text);
        mc.styleButton(newButton, false);
        mc.styleButton(editButton, false);
        mc.styleButton(deleteButton, false);

        LocalDate day = model.getCurrentViewDate();
        dateLabel.setText(formatDate(day));

        listModel.clear();
        List<Event> ofDay = model.getEventsOn(day);
        for (Event e : ofDay) {
            listModel.addElement(e);
        }
        details.setText("");

        revalidate();
        repaint();
    }

    /** Fills the details area with the data of the selected event. */
    private void showDetails() {
        Event e = eventJList.getSelectedValue();
        if (e == null) {
            details.setText("");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Title: ").append(e.getTitle()).append('\n');
        sb.append("When: ").append(e.getTime())
          .append(" (").append(e.getDuration()).append(" min)\n");
        sb.append("Category: ").append(e.getCategory()).append('\n');
        if (e.getLocation() != null && !e.getLocation().isEmpty()) {
            sb.append("Location: ").append(e.getLocation()).append('\n');
        }
        sb.append("Repeats: ").append(e.getRecurrence()).append('\n');
        sb.append("Reminder: ").append(reminderText(e.getReminderLeadMinutes())).append('\n');

        if (e.getDescription() != null && !e.getDescription().isEmpty()) {
            sb.append("\nDescription:\n").append(e.getDescription()).append('\n');
        }
        if (!e.getAttendees().isEmpty()) {
            sb.append("\nAttendees:\n");
            for (Attendee a : e.getAttendees()) {
                sb.append("- ").append(a).append('\n');
            }
        }
        details.setText(sb.toString());
        details.setCaretPosition(0);
    }

    /** Opens the form to create a new event on the selected day. */
    public void openCreate() {
        EventFormDialog dialog =
            new EventFormDialog(owner, controller, null, model.getCurrentViewDate());
        dialog.setVisible(true);
        Event created = dialog.getResult();
        if (created == null) {
            return;
        }
        try {
            boolean ok = controller.createEvent(created);
            if (!ok) {
                JOptionPane.showMessageDialog(this,
                    "An event with this title already exists.",
                    "Could not create", JOptionPane.WARNING_MESSAGE);
            }
        } catch (StorageException ex) {
            showStorageError(ex);
        }
    }

    /** Opens the form to edit the selected event. */
    public void openEdit() {
        Event selected = eventJList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                "Select an event to edit.",
                "No event selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        LocalDate day = model.getCurrentViewDate();
        EventFormDialog dialog = new EventFormDialog(owner, controller, selected, day);
        dialog.setVisible(true);
        Event edited = dialog.getResult();
        if (edited == null) {
            return;
        }

        try {
            if (selected.isRecurring()) {
                int scope = askScope("edit");
                if (scope == SCOPE_THIS) {
                    controller.editOccurrence(selected, day, edited);
                } else if (scope == SCOPE_ALL) {
                    replaceWarningIfNeeded(controller.replaceEvent(selected, edited));
                }
            } else {
                replaceWarningIfNeeded(controller.replaceEvent(selected, edited));
            }
        } catch (StorageException ex) {
            showStorageError(ex);
        }
    }

    /** Deletes the selected event (asking about scope when it recurs). */
    private void doDelete() {
        Event selected = eventJList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                "Select an event to delete.",
                "No event selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        LocalDate day = model.getCurrentViewDate();
        try {
            if (selected.isRecurring()) {
                int scope = askScope("delete");
                if (scope == SCOPE_THIS) {
                    controller.deleteOccurrence(selected, day);
                } else if (scope == SCOPE_ALL) {
                    controller.deleteEvent(selected);
                }
            } else {
                int r = JOptionPane.showConfirmDialog(this,
                    "Delete the event \"" + selected.getTitle() + "\"?",
                    "Confirm deletion", JOptionPane.YES_NO_OPTION);
                if (r == JOptionPane.YES_OPTION) {
                    controller.deleteEvent(selected);
                }
            }
        } catch (StorageException ex) {
            showStorageError(ex);
        }
    }

    /**
     * Asks whether a recurring action applies to a single day or the whole series.
     *
     * @param action the verb shown to the user ("edit" / "delete")
     * @return {@link #SCOPE_THIS}, {@link #SCOPE_ALL} or the cancel index
     */
    private int askScope(String action) {
        Object[] options = { "Only this one", "Whole series", "Cancel" };
        return JOptionPane.showOptionDialog(this,
            "This event repeats. Do you want to " + action
                + " only this occurrence or the whole series?",
            "Recurring event",
            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, options, options[0]);
    }

    private void replaceWarningIfNeeded(boolean ok) {
        if (!ok) {
            JOptionPane.showMessageDialog(this,
                "Another event with this title already exists.",
                "Could not save", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showStorageError(StorageException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(),
            "File error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Turns a reminder lead time into readable text.
     *
     * @param minutes lead time in minutes
     * @return the readable label
     */
    private String reminderText(long minutes) {
        if (minutes <= 0)    return "No reminder";
        if (minutes == 10)   return "10 minutes before";
        if (minutes == 60)   return "1 hour before";
        if (minutes == 1440) return "1 day before";
        return minutes + " minutes before";
    }

    private String formatDate(LocalDate day) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", EN);
        return day.format(fmt);
    }

    /** Renders each list entry as "HH:mm  Title". */
    private class EventRenderer extends JLabel implements ListCellRenderer<Event> {
        EventRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Event> list, Event value,
                int index, boolean isSelected, boolean cellHasFocus) {
            ThemeColors mc = ThemeColors.of(model.getDarkMode());
            setText(value.getTime() + "  " + value.getTitle());
            if (isSelected) {
                setBackground(mc.selected);
            } else {
                setBackground(mc.panel);
            }
            setForeground(mc.text);
            return this;
        }
    }
}
