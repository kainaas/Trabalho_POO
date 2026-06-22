package View;

import javax.swing.JComponent;

/**
 * A swappable central view of the calendar (day, week, month or year). The
 * {@link CalendarView} keeps one instance per {@link ViewMode} and shows the one
 * that matches the model's current mode.
 */
public interface CalendarSubView {
    /** Rebuilds the view from the current model state (data and theme). */
    void refresh();

    /**
     * @return the Swing component to place in the window
     */
    JComponent getComponent();
}
