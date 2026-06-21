package Model;

import View.ViewMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


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

    public boolean addEvent(Event e) {

        if(eventList.isEmpty()
        || getEventByTitle(e.getTitle()) == null) {

            eventList.add(e);

            firePropertyChange(
                "eventAdded",
                null,
                e
            );
            return true;
        }
        return false; //there's already an event with this name
    }

    public void removeEvent(Event e) {
        eventList.remove(e);
        firePropertyChange(
            "eventRemoved",
            e,
            null
        );
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

    //all the events that happen in the day, already counting repetitions, in chronological order
    public List<Event> getEventsOn(LocalDate day) {
        List<Event> ofDay = new ArrayList<>();
        for (Event e : eventList) {
            if (e.occursOn(day)) {
                ofDay.add(e);
            }
        }
        ofDay.sort(Comparator.comparing(Event::getTime));
        return ofDay;
    }

    public boolean hasEventsOn(LocalDate day) {
        for (Event e : eventList) {
            if (e.occursOn(day)) {
                return true;
            }
        }
        return false;
    }

    // how many events happen on the given day (counting recurrences)
    public int countEventsOn(LocalDate day) {
        int count = 0;
        for (Event e : eventList) {
            if (e.occursOn(day)) {
                count++;
            }
        }
        return count;
    }

    //searches for key-word in title, description, local or category
    public List<Event> searchEvents(String keyword) {
        List<Event> resultado = new ArrayList<>();
        if (keyword == null) {
            return resultado;
        }
        String alvo = keyword.trim().toLowerCase();
        if (alvo.isEmpty()) {
            return resultado;
        }
        for (Event e : eventList) {
            if (matches(e.getTitle(), alvo)
            || matches(e.getDescription(), alvo)
            || matches(e.getLocation(), alvo)
            || matches(e.getCategory(), alvo)) {
                resultado.add(e);
            }
        }
        return resultado;
    }

    private boolean matches(String campo, String alvo) {
        return campo != null && campo.toLowerCase().contains(alvo);
    }

    // adds without checking repeated title (used to edit only one ocurrence of an event)
    public void addEventForced(Event e) {
        eventList.add(e);
        firePropertyChange(
            "eventAdded",
            null,
            e
        );
    }

    // removes only on ocurrence of a recurrent event (leaves the others)
    public void removeOccurrence(Event master, LocalDate day) {
        master.addExceptionDate(day);
        firePropertyChange(
            "eventRemoved",
            master,
            null
        );
    }

    // copy of the list
    public List<Event> getAllEvents() {
        return new ArrayList<>(eventList);
    }

    // replaces the entire list
    public void loadEvents(List<Event> events) {
        eventList.clear();
        eventList.addAll(events);
        firePropertyChange(
            "eventAdded",
            null,
            null
        );
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
