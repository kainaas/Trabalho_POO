package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.YearMonth;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import Controller.Controller;
import Model.CalendarModel;

/**
 * Month view: a grid of the whole month. Each cell is a clickable day; days that
 * have events are highlighted (and show a small badge with the event count) and
 * so are today and the selected day. It always shows the month of the model's
 * current view date.
 */
public class CalendarMonthPanel extends JPanel implements CalendarSubView {
    private static final String[] WEEK_DAYS = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

    private final Controller controller;
    private final CalendarModel model;
    private final JPanel header;
    private final JPanel grid;

    /**
     * Builds the month view.
     *
     * @param controller the controller used to react to clicks
     */
    public CalendarMonthPanel(Controller controller) {
        this.controller = controller;
        this.model = controller.getModel();

        setLayout(new BorderLayout());
        header = new JPanel(new GridLayout(1, 7));
        grid = new JPanel(new GridLayout(0, 7, 2, 2));
        grid.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        add(header, BorderLayout.NORTH);
        add(grid, BorderLayout.CENTER);

        refresh();
    }

    /** @return this panel */
    @Override
    public JComponent getComponent() {
        return this;
    }

    /** Rebuilds the header and the day grid from the current model state. */
    @Override
    public void refresh() {
        ThemeColors mc = ThemeColors.of(model.getDarkMode());
        setBackground(mc.background);
        header.setBackground(mc.background);
        grid.setBackground(mc.background);

        buildHeader(mc);
        buildGrid(mc);

        revalidate();
        repaint();
    }

    private void buildHeader(ThemeColors mc) {
        header.removeAll();
        for (String dayName : WEEK_DAYS) {
            JLabel label = new JLabel(dayName, SwingConstants.CENTER);
            label.setFont(new Font("SansSerif", Font.BOLD, 14));
            label.setForeground(mc.subtleText);
            header.add(label);
        }
    }

    private void buildGrid(ThemeColors mc) {
        grid.removeAll();

        LocalDate viewDate = model.getCurrentViewDate();
        YearMonth month = YearMonth.from(viewDate);
        LocalDate firstDay = month.atDay(1);

        // How many blank cells precede day 1 (Sunday is the first column).
        int leading = firstDay.getDayOfWeek().getValue() % 7;
        for (int i = 0; i < leading; i++) {
            JPanel empty = new JPanel();
            empty.setBackground(mc.background);
            grid.add(empty);
        }

        LocalDate today = LocalDate.now();
        for (int day = 1; day <= month.lengthOfMonth(); day++) {
            LocalDate date = month.atDay(day);
            grid.add(makeDayCell(date, today, viewDate, mc));
        }
    }

    /**
     * Builds a single clickable day cell.
     *
     * @param date     the day the cell represents
     * @param today    today's date (for highlighting)
     * @param selected the selected date (for highlighting)
     * @param mc       the active theme
     * @return the day cell button
     */
    private JButton makeDayCell(LocalDate date, LocalDate today, LocalDate selected, ThemeColors mc) {
        int eventCount = model.countEventsOn(date);
        String label = "<html>" + date.getDayOfMonth();
        if (eventCount > 0) {
            // Small badge with the number of events on the day.
            label += " <sup>" + eventCount + "</sup>";
        }
        label += "</html>";

        JButton cell = new JButton(label);
        cell.setVerticalAlignment(SwingConstants.TOP);
        cell.setHorizontalAlignment(SwingConstants.LEFT);
        cell.setFocusPainted(false);
        cell.setOpaque(true);
        cell.setContentAreaFilled(true);
        cell.setBorderPainted(true);
        cell.setForeground(mc.text);
        if (eventCount > 0) {
            cell.setToolTipText(eventCount + (eventCount == 1 ? " event" : " events"));
        }

        Color background = mc.panel;
        if (model.hasEventsOn(date)) {
            background = mc.hasEvents;
        }
        if (date.equals(today)) {
            background = mc.today;
        }
        if (date.equals(selected)) {
            background = mc.selected;
        }
        cell.setBackground(background);
        cell.setBorder(BorderFactory.createLineBorder(mc.gridLine));

        cell.addActionListener(e -> controller.selectDate(date));
        return cell;
    }
}
