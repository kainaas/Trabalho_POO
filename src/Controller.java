import java.time.LocalDate;

public class Controller {
    private LocalDate viewDate; //Date which the view shows
    private boolean darkMode;
    private Event eventSelectedToEdit;
    private ViewMode currentMode;

    public Controller() {
        this.viewDate = getCurrentDate(); //initialize with current date
        this.eventSelectedToEdit = null;
        this.darkMode = false;
        this.currentMode = ViewMode.DAY;
    }

    public void setViewDate(LocalDate date) {
        this.viewDate = date;
    }

    public void setDarkMode(boolean b) {
        this.darkMode = b;
    }

    public void setEventSelectedToEdit(Event event) {
        this.eventSelectedToEdit = event;
    }

    public void setViewMode(ViewMode mode) {
        this.currentMode = mode;
    }

    public ViewMode getCurrentMode() {
        return currentMode;
    }

    public LocalDate getViewDate() {
        return viewDate;
    }

    public Event getEventSelectedToEdit() {
        return eventSelectedToEdit;
    }

    public boolean getDarkMode() {
        return darkMode;
    }

    public LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    public int Test(int k) {
        return k;
    }
}