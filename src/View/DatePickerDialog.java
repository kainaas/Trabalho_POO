package View;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Small modal calendar used to pick a date with the mouse instead of typing it
 * into spinners. This is the calendar-picker suggestion raised in the project
 * feedback. The chosen date is read back with {@link #getPickedDate()}.
 */
public class DatePickerDialog extends JDialog {
    private static final String[] WEEK_DAYS = { "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa" };

    private YearMonth shownMonth;
    private LocalDate picked;
    private final boolean dark;

    private final JLabel monthLabel;
    private final JPanel grid;

    /**
     * Opens the picker positioned on a starting date.
     *
     * @param owner   the parent window
     * @param initial the date to show first (defaults to today if {@code null})
     * @param dark    whether the dark theme is active
     */
    public DatePickerDialog(Window owner, LocalDate initial, boolean dark) {
        super(owner, "Pick a date", ModalityType.APPLICATION_MODAL);
        this.dark = dark;
        LocalDate start = initial != null ? initial : LocalDate.now();
        this.shownMonth = YearMonth.from(start);
        this.picked = start;

        ThemeColors mc = ThemeColors.of(dark);
        JPanel root = new JPanel(new BorderLayout(0, 6));
        root.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        root.setBackground(mc.background);

        JButton prev = new JButton("<");
        JButton next = new JButton(">");
        mc.styleButton(prev, false);
        mc.styleButton(next, false);
        prev.addActionListener(e -> { shownMonth = shownMonth.minusMonths(1); rebuild(); });
        next.addActionListener(e -> { shownMonth = shownMonth.plusMonths(1); rebuild(); });

        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        monthLabel.setForeground(mc.text);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(mc.background);
        header.add(prev, BorderLayout.WEST);
        header.add(monthLabel, BorderLayout.CENTER);
        header.add(next, BorderLayout.EAST);

        grid = new JPanel(new GridLayout(0, 7, 2, 2));
        grid.setBackground(mc.background);

        root.add(header, BorderLayout.NORTH);
        root.add(grid, BorderLayout.CENTER);
        setContentPane(root);

        rebuild();
        setPreferredSize(new Dimension(280, 260));
        pack();
        setLocationRelativeTo(owner);
    }

    /** Rebuilds the weekday header and the day buttons for the shown month. */
    private void rebuild() {
        ThemeColors mc = ThemeColors.of(dark);
        grid.removeAll();

        monthLabel.setText(shownMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
            + " " + shownMonth.getYear());

        for (String wd : WEEK_DAYS) {
            JLabel h = new JLabel(wd, SwingConstants.CENTER);
            h.setForeground(mc.subtleText);
            h.setFont(new Font("SansSerif", Font.BOLD, 11));
            grid.add(h);
        }

        int leading = shownMonth.atDay(1).getDayOfWeek().getValue() % 7;
        for (int i = 0; i < leading; i++) {
            JLabel blank = new JLabel("");
            blank.setOpaque(true);
            blank.setBackground(mc.background);
            grid.add(blank);
        }

        LocalDate today = LocalDate.now();
        for (int day = 1; day <= shownMonth.lengthOfMonth(); day++) {
            LocalDate date = shownMonth.atDay(day);
            JButton cell = new JButton(String.valueOf(day));
            cell.setMargin(new java.awt.Insets(1, 1, 1, 1));
            mc.styleButton(cell, date.equals(picked));
            if (date.equals(today) && !date.equals(picked)) {
                cell.setBackground(mc.today);
                cell.setForeground(mc.text);
            }
            cell.addActionListener(e -> { picked = date; dispose(); });
            grid.add(cell);
        }

        grid.revalidate();
        grid.repaint();
    }

    /**
     * @return the date the user picked (or the initial date if the window was
     *         closed without choosing a different day)
     */
    public LocalDate getPickedDate() {
        return picked;
    }
}
