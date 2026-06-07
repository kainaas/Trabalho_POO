package View;

/**
 * Determines the main panel view.
 */
public enum ViewMode {
    DAY(0), WEEK(1), MONTH(2), YEAR(3);

    private int mode;

    private ViewMode(int mode) {
        this.mode = mode;
    }

    public int getViewMode() {
        return mode;
    }

    @Override
    public String toString() {
        String a = "";
        switch (mode) {
            case 0: a = "Day"; break;
            case 1: a = "Week"; break;
            case 2: a = "Month"; break;
            case 3: a = "Year"; break;
        };
        return a;
    }
}
