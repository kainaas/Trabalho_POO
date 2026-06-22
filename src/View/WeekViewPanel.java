package View;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import Controller.Controller;
import Model.CalendarModel;
import Model.Event;

/**
 * Week view: seven columns (Sunday to Saturday) for the week that contains the
 * model's current view date. Each column shows the day's header and the list of
 * its events; clicking a header or an event selects that day.
 */
public class WeekViewPanel extends JPanel implements CalendarSubView {
    private static final String[] WEEK_DAYS = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
    private static final DateTimeFormatter DAY_FMT =
        DateTimeFormatter.ofPattern("dd/MM", Locale.ENGLISH);

    private final Controller controller;
    private final CalendarModel model;

    /**
     * Builds the week view.
     *
     * @param controller the controller used to react to clicks
     */
    public WeekViewPanel(Controller controller) {
        this.controller = controller;
        this.model = controller.getModel();
        setLayout(new GridLayout(1, 7, 2, 0));
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        refresh();
    }

    /** @return this panel */
    @Override
    public JComponent getComponent() {
        return this;
    }

    /** @return the Sunday that starts the week of the current view date */
    private LocalDate weekStart() {
        LocalDate d = model.getCurrentViewDate();
        return d.minusDays(d.getDayOfWeek().getValue() % 7);
    }

    /** Rebuilds the seven day columns from the current model state. */
    @Override
    public void refresh() {
        ThemeColors mc = ThemeColors.of(model.getDarkMode());
        setBackground(mc.background);
        removeAll();

        LocalDate start = weekStart();
        LocalDate today = LocalDate.now();
        LocalDate selected = model.getCurrentViewDate();

        for (int i = 0; i < 7; i++) {
            LocalDate date = start.plusDays(i);
            add(buildColumn(date, today, selected, mc));
        }
        revalidate();
        repaint();
    }

    /**
     * Builds one day column.
     *
     * @param date     the day the column represents
     * @param today    today's date (for highlighting)
     * @param selected the selected date (for highlighting)
     * @param mc       the active theme
     * @return the column panel
     */
    private JPanel buildColumn(LocalDate date, LocalDate today, LocalDate selected, ThemeColors mc) {
        JPanel column = new JPanel(new BorderLayout(0, 4));
        column.setBackground(mc.background);

        JButton header = new JButton(WEEK_DAYS[date.getDayOfWeek().getValue() % 7]
            + " " + date.format(DAY_FMT));
        boolean isSelected = date.equals(selected);
        mc.styleButton(header, isSelected);
        if (date.equals(today)) {
            header.setBackground(mc.today);
            header.setForeground(mc.text);
        }
        header.addActionListener(e -> controller.selectDate(date));

        JPanel events = new JPanel();
        events.setLayout(new BoxLayout(events, BoxLayout.Y_AXIS));
        events.setBackground(mc.panel);

        List<Event> dayEvents = model.getEventsOn(date);
        if (dayEvents.isEmpty()) {
            JLabel empty = new JLabel("—", SwingConstants.CENTER);
            empty.setForeground(mc.subtleText);
            empty.setAlignmentX(CENTER_ALIGNMENT);
            events.add(empty);
        } else {
            for (Event ev : dayEvents) {
                events.add(eventChip(ev, date, mc));
            }
        }

        JScrollPane scroll = new JScrollPane(events,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createLineBorder(mc.gridLine));
        scroll.getViewport().setBackground(mc.panel);

        column.add(header, BorderLayout.NORTH);
        column.add(scroll, BorderLayout.CENTER);
        return column;
    }

    /**
     * Builds a clickable chip representing one event inside a day column.
     *
     * @param ev   the event
     * @param date the day of the column
     * @param mc   the active theme
     * @return the event chip
     */
    private JButton eventChip(Event ev, LocalDate date, ThemeColors mc) {
        JButton chip = new JButton("<html>" + ev.getTime() + "<br>" + ev.getTitle() + "</html>");
        chip.setHorizontalAlignment(SwingConstants.LEFT);
        chip.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        chip.setFont(new Font("SansSerif", Font.PLAIN, 11));
        mc.styleButton(chip, false);
        chip.setBackground(mc.hasEvents);
        chip.setToolTipText(ev.getTitle() + " (" + ev.getCategory() + ")");
        chip.addActionListener(e -> controller.selectDate(date));
        return chip;
    }
}
