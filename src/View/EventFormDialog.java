package View;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import Controller.Controller;
import Model.Attendee;
import Model.Event;
import Model.EventValidationException;
import Model.Recurrence;

/**
 * Form window to create or edit an event. It gathers the data, asks the
 * {@link Controller} to validate it and, when everything is correct, stores the
 * finished event in {@code result}. The caller reads it back with
 * {@link #getResult()}.
 */
public class EventFormDialog extends JDialog {

    /** Reminder options shown in the combo box. */
    private static final String[] REMINDER_LABELS = {
        "No reminder", "10 minutes before", "1 hour before", "1 day before"
    };
    /** Lead time, in minutes, for each reminder option. */
    private static final long[] REMINDER_MINUTES = { 0, 10, 60, 1440 };

    private static final String[] CATEGORIES = {
        "Meeting", "Birthday", "Appointment", "Other"
    };

    private final Controller controller;

    private JTextField titleField;
    private JSpinner daySpinner, monthSpinner, yearSpinner, hourSpinner, minuteSpinner, durationSpinner;
    private JComboBox<String> categoryCombo;
    private JTextField locationField;
    private JTextArea descriptionArea;
    private JComboBox<String> reminderCombo;
    private JComboBox<Recurrence> recurrenceCombo;
    private DefaultListModel<Attendee> attendeesModel;

    private Event result;

    /**
     * Builds the form.
     *
     * @param owner       the parent window
     * @param controller  the controller used to validate/build the event
     * @param existing    the event being edited, or {@code null} to create one
     * @param displayDate the date to pre-fill (the selected day)
     */
    public EventFormDialog(Frame owner, Controller controller, Event existing, LocalDate displayDate) {
        super(owner, existing == null ? "New event" : "Edit event", true);
        this.controller = controller;

        buildComponents();
        buildLayout();
        if (existing != null) {
            fill(existing, displayDate);
        } else {
            fillNew(displayDate);
        }

        pack();
        setLocationRelativeTo(owner);
    }

    private void buildComponents() {
        titleField = new JTextField(24);

        daySpinner    = new JSpinner(new SpinnerNumberModel(1, 1, 31, 1));
        monthSpinner  = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        yearSpinner   = new JSpinner(new SpinnerNumberModel(2026, 1970, 3000, 1));
        yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "#"));
        hourSpinner   = new JSpinner(new SpinnerNumberModel(9, 0, 23, 1));
        minuteSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        durationSpinner = new JSpinner(new SpinnerNumberModel(60, 1, 100000, 5));

        categoryCombo = new JComboBox<>(CATEGORIES);
        locationField = new JTextField(24);
        descriptionArea = new JTextArea(4, 24);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        reminderCombo = new JComboBox<>(REMINDER_LABELS);
        recurrenceCombo = new JComboBox<>(
            new Recurrence[] { Recurrence.NONE, Recurrence.DAILY, Recurrence.WEEKLY, Recurrence.MONTHLY });

        attendeesModel = new DefaultListModel<>();
    }

    private void buildLayout() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;

        int row = 0;
        addRow(form, c, row++, "Title:", titleField);
        addRow(form, c, row++, "Date (day/month/year):", datePanel());
        addRow(form, c, row++, "Time (hour:min):", timePanel());
        addRow(form, c, row++, "Duration (min):", durationSpinner);
        addRow(form, c, row++, "Category:", categoryCombo);
        addRow(form, c, row++, "Location:", locationField);
        addRow(form, c, row++, "Description:", new JScrollPane(descriptionArea));
        addRow(form, c, row++, "Reminder:", reminderCombo);
        addRow(form, c, row++, "Repeats:", recurrenceCombo);
        addRow(form, c, row++, "Attendees:", attendeesPanel());

        JButton ok = new JButton("Save");
        JButton cancel = new JButton("Cancel");
        ok.addActionListener(e -> onSave());
        cancel.addActionListener(e -> { result = null; dispose(); });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancel);
        buttons.add(ok);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    private void addRow(JPanel form, GridBagConstraints c, int row, String label, java.awt.Component field) {
        c.gridx = 0;
        c.gridy = row;
        c.fill = GridBagConstraints.NONE;
        form.add(new JLabel(label), c);
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        form.add(field, c);
    }

    private JPanel datePanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.add(daySpinner);
        p.add(new JLabel("/"));
        p.add(monthSpinner);
        p.add(new JLabel("/"));
        p.add(yearSpinner);

        JButton pick = new JButton("📅"); // calendar emoji
        pick.setToolTipText("Pick from a calendar");
        pick.setMargin(new Insets(2, 6, 2, 6));
        pick.addActionListener(e -> openDatePicker());
        p.add(pick);
        return p;
    }

    /** Opens the calendar picker and copies the chosen date into the spinners. */
    private void openDatePicker() {
        LocalDate current = LocalDate.of(
            (Integer) yearSpinner.getValue(),
            (Integer) monthSpinner.getValue(),
            (Integer) daySpinner.getValue());
        DatePickerDialog picker = new DatePickerDialog(
            this, current, controller.getModel().getDarkMode());
        picker.setVisible(true);
        LocalDate chosen = picker.getPickedDate();
        if (chosen != null) {
            yearSpinner.setValue(chosen.getYear());
            monthSpinner.setValue(chosen.getMonthValue());
            daySpinner.setValue(chosen.getDayOfMonth());
        }
    }

    private JPanel timePanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.add(hourSpinner);
        p.add(new JLabel(":"));
        p.add(minuteSpinner);
        return p;
    }

    private JPanel attendeesPanel() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        JList<Attendee> list = new JList<>(attendeesModel);
        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(220, 60));

        JButton add = new JButton("Add");
        JButton remove = new JButton("Remove");
        add.addActionListener(e -> promptAttendee());
        remove.addActionListener(e -> {
            int i = list.getSelectedIndex();
            if (i >= 0) {
                attendeesModel.remove(i);
            }
        });

        JPanel side = new JPanel(new GridLayout(2, 1, 0, 5));
        side.add(add);
        side.add(remove);

        p.add(scroll, BorderLayout.CENTER);
        p.add(side, BorderLayout.EAST);
        return p;
    }

    /** Prompts for a new attendee and adds it to the list after validation. */
    private void promptAttendee() {
        JTextField name = new JTextField();
        JTextField email = new JTextField();
        JPanel p = new JPanel(new GridLayout(0, 1, 0, 3));
        p.add(new JLabel("Name:"));
        p.add(name);
        p.add(new JLabel("Email:"));
        p.add(email);

        int r = JOptionPane.showConfirmDialog(
            this, p, "Add attendee", JOptionPane.OK_CANCEL_OPTION);
        if (r != JOptionPane.OK_OPTION) {
            return;
        }

        String n = name.getText().trim();
        String e = email.getText().trim();
        if (n.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "The attendee's name cannot be empty.",
                "Invalid data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!e.isEmpty() && (!e.contains("@") || !e.contains("."))) {
            JOptionPane.showMessageDialog(this,
                "The e-mail does not look valid.",
                "Invalid data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        attendeesModel.addElement(new Attendee(n, e));
    }

    /** Collects the form data, validates it through the controller and closes. */
    private void onSave() {
        int year   = (Integer) yearSpinner.getValue();
        int month  = (Integer) monthSpinner.getValue();
        int day    = (Integer) daySpinner.getValue();
        int hour   = (Integer) hourSpinner.getValue();
        int minute = (Integer) minuteSpinner.getValue();
        long duration = ((Number) durationSpinner.getValue()).longValue();

        long reminder = REMINDER_MINUTES[reminderCombo.getSelectedIndex()];
        Recurrence recurrence = (Recurrence) recurrenceCombo.getSelectedItem();

        List<Attendee> attendees = new ArrayList<>();
        for (int i = 0; i < attendeesModel.size(); i++) {
            attendees.add(attendeesModel.get(i));
        }

        try {
            result = controller.buildEvent(year, month, day, hour, minute, duration,
                titleField.getText(), (String) categoryCombo.getSelectedItem(),
                locationField.getText(), descriptionArea.getText(),
                reminder, recurrence, attendees);
            dispose();
        } catch (EventValidationException ex) {
            // Show the error and keep the window open so the user can fix it.
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Invalid data", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void fillNew(LocalDate displayDate) {
        LocalDate d = displayDate != null ? displayDate : LocalDate.now();
        daySpinner.setValue(d.getDayOfMonth());
        monthSpinner.setValue(d.getMonthValue());
        yearSpinner.setValue(d.getYear());
    }

    private void fill(Event e, LocalDate displayDate) {
        LocalDate d = displayDate != null ? displayDate : e.getDate();
        LocalTime t = e.getTime();

        titleField.setText(e.getTitle());
        daySpinner.setValue(d.getDayOfMonth());
        monthSpinner.setValue(d.getMonthValue());
        yearSpinner.setValue(d.getYear());
        hourSpinner.setValue(t.getHour());
        minuteSpinner.setValue(t.getMinute());
        durationSpinner.setValue((int) e.getDuration());
        categoryCombo.setSelectedItem(e.getCategory());
        locationField.setText(e.getLocation());
        descriptionArea.setText(e.getDescription());
        recurrenceCombo.setSelectedItem(e.getRecurrence());

        long lead = e.getReminderLeadMinutes();
        for (int i = 0; i < REMINDER_MINUTES.length; i++) {
            if (REMINDER_MINUTES[i] == lead) {
                reminderCombo.setSelectedIndex(i);
                break;
            }
        }
        for (Attendee a : e.getAttendees()) {
            attendeesModel.addElement(a);
        }
    }

    /**
     * @return the event built from the form, or {@code null} if cancelled
     */
    public Event getResult() {
        return result;
    }
}
