package Model;

import java.util.ArrayList;
import java.util.List;
import View.ViewMode;
import java.time.LocalDate;


//TODO: usar JavaBeans para que modificações no Model seja notificadas para view

/**
 * Holds all the information that the program needs. Cannot have events with same title
 */
public class CalendarModel extends  AbstractModel {
    private LocalDate currentViewDate; //Date which the view shows
    private boolean darkMode;
    private Event eventSelected;
    private ViewMode currentMode;
    private List<Event> eventList; 

    public CalendarModel() {
        super();
        this.currentViewDate = getCurrentDate(); //initialize with current date
        this.eventSelected = null;
        this.darkMode = false;
        this.currentMode = ViewMode.DAY;
        this.eventList = new ArrayList<>();
    }

    public void addEvent(Event e) {

        if(eventList.isEmpty()
        || getEventByTitle(e.getTitle()) == null) {

            eventList.add(e);

            firePropertyChange(
                "eventAdded",
                null,
                e
            );
        }
    }

    public void removeEvent(Event e) {
        eventList.remove(e);
    }

    public Event getEventByTitle(String title) {
        title = title.trim().toLowerCase();
        for(int i = 0; i < eventList.size(); i++) {
            if(eventList.get(i).getTitle().toLowerCase().trim().equals(title)){
                return eventList.get(i);
            }
        }
        return null; //if the event is not in the list
    }


    public LocalDate nextDay(LocalDate date) {
        return date.plusDays(1);
    }

    public LocalDate nextWeek(LocalDate date) {
        return date.plusWeeks(1);
    }

    public LocalDate nextMonth(LocalDate date) {
        return date.plusMonths(1);
    }

    public LocalDate nextYear(LocalDate date) {
        return date.plusYears(1);
    }

    public void setViewDate(LocalDate date) {
        LocalDate oldDate = this.currentViewDate;
        this.currentViewDate = date;

        firePropertyChange(
            "currentViewDate",
            oldDate,
            date
        );
    }

    public void setDarkMode(boolean darkMode) {
        boolean oldDarkMode = this.darkMode;
        this.darkMode = darkMode;

        firePropertyChange(
            "darkMode",
            oldDarkMode,
            darkMode
        );
    }

    public void setEventSelected(Event event) {
        Event oldEvent = this.eventSelected;
        this.eventSelected = event;

        firePropertyChange(
            "eventSelected",
            oldEvent,
            event
        );
    }

    public void setViewMode(ViewMode mode) {
        ViewMode oldMode = this.currentMode;
        this.currentMode = mode;

        firePropertyChange(
            "currentMode",
            oldMode,
            mode
        );
    }

    public ViewMode getCurrentMode() {
        return currentMode;
    }

    public LocalDate getCurrentViewDate() {
        return currentViewDate;
    }

    public Event getEventSelected() {
        return eventSelected;
    }

    public boolean getDarkMode() {
        return darkMode;
    }

    public LocalDate getCurrentDate() {
        return LocalDate.now();
    }
}
