package Model;

import View.ViewMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Holds all of the state the application needs: the event list, the currently
 * displayed date, the active view mode and the dark-mode flag. Two events cannot
 * share the same title.
 *
 * <p>It extends {@link AbstractModel}, so every relevant change fires a
 * {@link java.beans.PropertyChangeEvent} that the view layer listens to
 * (Observer pattern).</p>
 */
public class CalendarModel extends AbstractModel {
    /** Date currently shown by the view. */
    private LocalDate currentViewDate;
    private boolean darkMode;
    private Event eventSelected;
    private ViewMode currentMode;
    private List<Event> eventList;

    /** Creates an empty model positioned at today's date, in month/light mode. */
    public CalendarModel() {
        super();
        this.currentViewDate = getCurrentDate();
        this.eventSelected = null;
        this.darkMode = false;
        this.currentMode = ViewMode.MONTH;
        this.eventList = new ArrayList<>();
    }

    /**
     * Adds a new event unless another event already has the same title.
     *
     * @param e the event to add
     * @return {@code true} if it was added, {@code false} if the title was taken
     */
    public boolean addEvent(Event e) {
        if (eventList.isEmpty() || getEventByTitle(e.getTitle()) == null) {
            eventList.add(e);
            firePropertyChange("eventAdded", null, e);
            return true;
        }
        return false; // an event with this title already exists
    }

    /**
     * Removes an event from the list.
     *
     * @param e the event to remove
     */
    public void removeEvent(Event e) {
        eventList.remove(e);
        firePropertyChange("eventRemoved", e, null);
    }

    /**
     * Looks up an event by its title (case-insensitive).
     *
     * @param title the title to search for
     * @return the matching event, or {@code null} if none exists
     */
    public Event getEventByTitle(String title) {
        title = title.trim().toLowerCase();
        for (int i = 0; i < eventList.size(); i++) {
            if (eventList.get(i).getTitle().toLowerCase().trim().equals(title)) {
                return eventList.get(i);
            }
        }
        return null;
    }

    /**
     * Returns every event that happens on the given day (recurrences included),
     * sorted chronologically.
     *
     * @param day the day to query
     * @return the events of that day in time order
     */
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

    /**
     * Tells whether any event occurs on the given day.
     *
     * @param day the day to query
     * @return {@code true} if at least one event occurs that day
     */
    public boolean hasEventsOn(LocalDate day) {
        for (Event e : eventList) {
            if (e.occursOn(day)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Counts how many events occur on the given day (recurrences included).
     *
     * @param day the day to query
     * @return the number of events on that day
     */
    public int countEventsOn(LocalDate day) {
        int count = 0;
        for (Event e : eventList) {
            if (e.occursOn(day)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Searches every event for a keyword in its title, description, location or
     * category.
     *
     * @param keyword the text to look for
     * @return the matching events (empty if {@code keyword} is null or blank)
     */
    public List<Event> searchEvents(String keyword) {
        List<Event> result = new ArrayList<>();
        if (keyword == null) {
            return result;
        }
        String target = keyword.trim().toLowerCase();
        if (target.isEmpty()) {
            return result;
        }
        for (Event e : eventList) {
            if (matches(e.getTitle(), target)
                    || matches(e.getDescription(), target)
                    || matches(e.getLocation(), target)
                    || matches(e.getCategory(), target)) {
                result.add(e);
            }
        }
        return result;
    }

    private boolean matches(String field, String target) {
        return field != null && field.toLowerCase().contains(target);
    }

    /**
     * Adds an event without checking for a duplicate title. Used when editing a
     * single occurrence of a recurring series (which produces a one-off event
     * that may share the master's title).
     *
     * @param e the event to add
     */
    public void addEventForced(Event e) {
        eventList.add(e);
        firePropertyChange("eventAdded", null, e);
    }

    /**
     * Removes a single occurrence of a recurring event, keeping the rest of the
     * series.
     *
     * @param master the recurring event
     * @param day    the day of the occurrence to drop
     */
    public void removeOccurrence(Event master, LocalDate day) {
        master.addExceptionDate(day);
        firePropertyChange("eventRemoved", master, null);
    }

    /** @return a defensive copy of the event list */
    public List<Event> getAllEvents() {
        return new ArrayList<>(eventList);
    }

    /**
     * Replaces the whole event list (used when loading from disk).
     *
     * @param events the events to load
     */
    public void loadEvents(List<Event> events) {
        eventList.clear();
        eventList.addAll(events);
        firePropertyChange("eventAdded", null, null);
    }

    /**
     * @param date a reference date
     * @return the day after {@code date}
     */
    public LocalDate nextDay(LocalDate date) {
        return date.plusDays(1);
    }

    /**
     * @param date a reference date
     * @return the same weekday one week later
     */
    public LocalDate nextWeek(LocalDate date) {
        return date.plusWeeks(1);
    }

    /**
     * @param date a reference date
     * @return the same day one month later
     */
    public LocalDate nextMonth(LocalDate date) {
        return date.plusMonths(1);
    }

    /**
     * @param date a reference date
     * @return the same day one year later
     */
    public LocalDate nextYear(LocalDate date) {
        return date.plusYears(1);
    }

    /**
     * Sets the date the view should display and notifies observers.
     *
     * @param date the new view date
     */
    public void setViewDate(LocalDate date) {
        LocalDate oldDate = this.currentViewDate;
        this.currentViewDate = date;
        firePropertyChange("currentViewDate", oldDate, date);
    }

    /**
     * Sets the dark-mode flag and notifies observers.
     *
     * @param darkMode {@code true} for the dark theme
     */
    public void setDarkMode(boolean darkMode) {
        boolean oldDarkMode = this.darkMode;
        this.darkMode = darkMode;
        firePropertyChange("darkMode", oldDarkMode, darkMode);
    }

    /**
     * Sets the currently selected event and notifies observers.
     *
     * @param event the selected event (may be {@code null})
     */
    public void setEventSelected(Event event) {
        Event oldEvent = this.eventSelected;
        this.eventSelected = event;
        firePropertyChange("eventSelected", oldEvent, event);
    }

    /**
     * Sets the active view mode and notifies observers.
     *
     * @param mode the new view mode
     */
    public void setViewMode(ViewMode mode) {
        ViewMode oldMode = this.currentMode;
        this.currentMode = mode;
        firePropertyChange("currentMode", oldMode, mode);
    }

    /** @return the active view mode */
    public ViewMode getCurrentMode() {
        return currentMode;
    }

    /** @return the date currently displayed */
    public LocalDate getCurrentViewDate() {
        return currentViewDate;
    }

    /** @return the currently selected event, or {@code null} */
    public Event getEventSelected() {
        return eventSelected;
    }

    /** @return {@code true} if the dark theme is active */
    public boolean getDarkMode() {
        return darkMode;
    }

    /** @return today's date */
    public LocalDate getCurrentDate() {
        return LocalDate.now();
    }
}
