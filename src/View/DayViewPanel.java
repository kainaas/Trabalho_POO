package View;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
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
 * Day view: an hour-by-hour agenda (00:00 to 23:00) of the model's current view
 * date. Each event is placed on the row of the hour at which it starts.
 */
public class DayViewPanel extends JPanel implements CalendarSubView {
    private static final DateTimeFormatter TITLE_FMT =
        DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale.ENGLISH);

    private final Controller controller;
    private final CalendarModel model;
    private final JLabel title;
    private final JPanel hours;

    /**
     * Builds the day view.
     *
     * @param controller the controller (kept for symmetry with the other views)
     */
    public DayViewPanel(Controller controller) {
        this.controller = controller;
        this.model = controller.getModel();

        setLayout(new BorderLayout(0, 6));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        title = new JLabel("", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        hours = new JPanel();
        hours.setLayout(new BoxLayout(hours, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(hours,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(title, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        refresh();
    }

    /** @return this panel */
    @Override
    public JComponent getComponent() {
        return this;
    }

    /** Rebuilds the 24 hour rows for the current day. */
    @Override
    public void refresh() {
        ThemeColors mc = ThemeColors.of(model.getDarkMode());
        setBackground(mc.background);
        title.setForeground(mc.text);

        LocalDate day = model.getCurrentViewDate();
        title.setText(day.format(TITLE_FMT));

        List<Event> dayEvents = model.getEventsOn(day);
        hours.removeAll();
        hours.setBackground(mc.background);

        for (int h = 0; h < 24; h++) {
            hours.add(buildHourRow(h, dayEvents, mc));
        }
        revalidate();
        repaint();
    }

    /**
     * Builds one hour row, listing the events that start in that hour.
     *
     * @param hour      the hour (0-23)
     * @param dayEvents the events of the day
     * @param mc        the active theme
     * @return the hour row panel
     */
    private JPanel buildHourRow(int hour, List<Event> dayEvents, ThemeColors mc) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(mc.background);
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, mc.gridLine));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel time = new JLabel(String.format("%02d:00", hour), SwingConstants.RIGHT);
        time.setPreferredSize(new Dimension(54, 0));
        time.setForeground(mc.subtleText);
        time.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JPanel slot = new JPanel();
        slot.setLayout(new BoxLayout(slot, BoxLayout.Y_AXIS));
        slot.setBackground(mc.background);

        for (Event ev : dayEvents) {
            if (ev.getTime().getHour() == hour) {
                JLabel chip = new JLabel(ev.getTime() + "  " + ev.getTitle()
                    + "  (" + ev.getDuration() + " min)");
                chip.setOpaque(true);
                chip.setBackground(mc.hasEvents);
                chip.setForeground(mc.text);
                chip.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
                chip.setToolTipText(ev.getCategory());
                slot.add(chip);
            }
        }

        row.add(time, BorderLayout.WEST);
        row.add(slot, BorderLayout.CENTER);
        return row;
    }
}
