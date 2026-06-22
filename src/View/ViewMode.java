package View;

/**
 * The granularity of the central calendar view.
 */
public enum ViewMode {
    /** Shows a single day. */
    DAY(0),
    /** Shows one week. */
    WEEK(1),
    /** Shows one month. */
    MONTH(2),
    /** Shows the twelve months of a year. */
    YEAR(3);

    private final int mode;

    private ViewMode(int mode) {
        this.mode = mode;
    }

    /** @return the numeric code of the mode */
    public int getViewMode() {
        return mode;
    }

    /** @return a human-readable label for the mode */
    @Override
    public String toString() {
        switch (mode) {
            case 0:  return "Day";
            case 1:  return "Week";
            case 2:  return "Month";
            case 3:  return "Year";
            default: return "";
        }
    }
}
