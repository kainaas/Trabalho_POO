package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import Controller.Controller;
import Model.CalendarModel;

/**
 * Year view: the twelve months of the model's current year shown as small grids.
 * Days that have events and today are highlighted. Clicking a month title jumps
 * to that month in the month view.
 */
public class YearViewPanel extends JPanel implements CalendarSubView {
    private static final String[] WEEK_INITIALS = { "S", "M", "T", "W", "T", "F", "S" };

    private final Controller controller;
    private final CalendarModel model;

    /**
     * Builds the year view.
     *
     * @param controller the controller used to react to clicks
     */
    public YearViewPanel(Controller controller) {
        this.controller = controller;
        this.model = controller.getModel();
        setLayout(new GridLayout(3, 4, 6, 6));
        setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        refresh();
    }

    /** @return this panel */
    @Override
    public JComponent getComponent() {
        return this;
    }

    /** Rebuilds the twelve mini-months from the current model state. */
    @Override
    public void refresh() {
        ThemeColors mc = ThemeColors.of(model.getDarkMode());
        setBackground(mc.background);
        removeAll();

        int year = model.getCurrentViewDate().getYear();
        for (int m = 1; m <= 12; m++) {
            add(buildMiniMonth(YearMonth.of(year, m), mc));
        }
        revalidate();
        repaint();
    }

    /**
     * Builds a single mini-month panel.
     *
     * @param ym the year-month to render
     * @param mc the active theme
     * @return the mini-month panel
     */
    private JPanel buildMiniMonth(YearMonth ym, ThemeColors mc) {
        JPanel panel = new JPanel(new BorderLayout(0, 2));
        panel.setBackground(mc.background);
        panel.setBorder(BorderFactory.createLineBorder(mc.gridLine));

        String name = ym.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        JButton title = new JButton(name);
        title.setFont(new Font("SansSerif", Font.BOLD, 12));
        mc.styleButton(title, false);
        title.addActionListener(e -> {
            controller.selectDate(ym.atDay(1));
            controller.setViewMode(ViewMode.MONTH);
        });

        JPanel grid = new JPanel(new GridLayout(0, 7, 1, 1));
        grid.setBackground(mc.background);

        for (String wd : WEEK_INITIALS) {
            JLabel h = new JLabel(wd, SwingConstants.CENTER);
            h.setFont(new Font("SansSerif", Font.PLAIN, 9));
            h.setForeground(mc.subtleText);
            grid.add(h);
        }

        int leading = ym.atDay(1).getDayOfWeek().getValue() % 7;
        for (int i = 0; i < leading; i++) {
            grid.add(new JLabel(""));
        }

        LocalDate today = LocalDate.now();
        for (int day = 1; day <= ym.lengthOfMonth(); day++) {
            LocalDate date = ym.atDay(day);
            grid.add(dayLabel(date, today, mc));
        }

        panel.add(title, BorderLayout.NORTH);
        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Builds a small day label, highlighting today and days that have events.
     *
     * @param date  the day
     * @param today today's date
     * @param mc    the active theme
     * @return the day label
     */
    private JLabel dayLabel(LocalDate date, LocalDate today, ThemeColors mc) {
        JLabel label = new JLabel(String.valueOf(date.getDayOfMonth()), SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 10));
        label.setOpaque(true);
        label.setForeground(mc.text);

        Color background = mc.background;
        if (model.hasEventsOn(date)) {
            background = mc.hasEvents;
        }
        if (date.equals(today)) {
            background = mc.today;
        }
        label.setBackground(background);
        if (model.hasEventsOn(date)) {
            int n = model.countEventsOn(date);
            label.setToolTipText(date + ": " + n + (n == 1 ? " event" : " events"));
        }
        return label;
    }
}
