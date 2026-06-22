package Controller;

import Model.Attendee;
import Model.CalendarModel;
import Model.Event;
import Model.EventStorage;
import Model.EventValidationException;
import Model.Recurrence;
import Model.StorageException;
import View.ViewMode;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;

/**
 * Connects the interface to the model. It validates input, creates, edits and
 * deletes events and asks the storage to persist them. It has no direct access
 * to screen elements: the view is notified through the model's property-change
 * events.
 */
public class Controller {
    private final CalendarModel model;
    private final EventStorage storage;

    /**
     * Creates the controller.
     *
     * @param model   the calendar model
     * @param storage the storage used to persist events
     */
    public Controller(CalendarModel model, EventStorage storage) {
        this.model = model;
        this.storage = storage;
    }

    /** @return the calendar model */
    public CalendarModel getModel() {
        return model;
    }

    /**
     * Builds an event from the form data, validating everything.
     *
     * @param year        starting year
     * @param month       starting month (1-12)
     * @param day         starting day of month
     * @param hour        starting hour (0-23)
     * @param minute      starting minute (0-59)
     * @param duration    duration in minutes
     * @param title       event title
     * @param category    event category
     * @param location    event location
     * @param description free-text description
     * @param reminderLead reminder lead time in minutes
     * @param recurrence  recurrence rule
     * @param attendees   list of attendees
     * @return the validated event
     * @throws EventValidationException if any field is invalid
     */
    public Event buildEvent(int year, int month, int day, int hour, int minute,
            long duration, String title, String category, String location,
            String description, long reminderLead, Recurrence recurrence,
            List<Attendee> attendees) throws EventValidationException {

        if (title == null || title.trim().isEmpty()) {
            throw new EventValidationException("The event title cannot be empty.");
        }
        if (duration <= 0) {
            throw new EventValidationException("The duration must be at least 1 minute.");
        }

        Event e;
        try {
            e = new Event(year, month, day, hour, minute,
                    duration, title, category, description);
        } catch (DateTimeException ex) {
            throw new EventValidationException(
                "Invalid date or time. Check the day, month and time.");
        }

        e.setLocation(location == null ? "" : location.trim());
        e.setReminderLeadMinutes(reminderLead);
        e.setRecurrence(recurrence);
        if (attendees != null) {
            for (Attendee a : attendees) {
                e.addAttendee(a);
            }
        }
        return e;
    }

    /**
     * Adds a new event.
     *
     * @param e the event to add
     * @return {@code false} if an event with this title already exists
     * @throws StorageException if saving fails
     */
    public boolean createEvent(Event e) throws StorageException {
        boolean added = model.addEvent(e);
        if (added) {
            save();
        }
        return added;
    }

    /**
     * Replaces an event with an edited version.
     *
     * @param oldEvent the event being replaced
     * @param newEvent the edited event
     * @return {@code false} if the new title already belongs to another event
     *         (in which case the old event is kept)
     * @throws StorageException if saving fails
     */
    public boolean replaceEvent(Event oldEvent, Event newEvent) throws StorageException {
        model.removeEvent(oldEvent);
        boolean ok = model.addEvent(newEvent);
        if (!ok) {
            model.addEvent(oldEvent);
            return false;
        }
        save();
        return true;
    }

    /**
     * Edits only the occurrence on the given day: it is taken out of the series
     * and turned into a stand-alone event.
     *
     * @param master the recurring event
     * @param day    the day of the occurrence being edited
     * @param edited the edited stand-alone event
     * @throws StorageException if saving fails
     */
    public void editOccurrence(Event master, LocalDate day, Event edited) throws StorageException {
        master.addExceptionDate(day);
        edited.setRecurrence(Recurrence.NONE);
        model.addEventForced(edited);
        save();
    }

    /**
     * Deletes an event entirely.
     *
     * @param e the event to delete
     * @throws StorageException if saving fails
     */
    public void deleteEvent(Event e) throws StorageException {
        model.removeEvent(e);
        save();
    }

    /**
     * Deletes only the occurrence on the given day, keeping the rest of the series.
     *
     * @param master the recurring event
     * @param day    the day of the occurrence to delete
     * @throws StorageException if saving fails
     */
    public void deleteOccurrence(Event master, LocalDate day) throws StorageException {
        model.removeOccurrence(master, day);
        save();
    }

    /**
     * Searches the events for a keyword.
     *
     * @param keyword the text to look for
     * @return the matching events
     */
    public List<Event> search(String keyword) {
        return model.searchEvents(keyword);
    }

    /**
     * Selects a day and clears the current event selection.
     *
     * @param day the day to select
     */
    public void selectDate(LocalDate day) {
        model.setViewDate(day);
        model.setEventSelected(null);
    }

    /**
     * Changes the active view mode.
     *
     * @param mode the new view mode
     */
    public void setViewMode(ViewMode mode) {
        model.setViewMode(mode);
    }

    /**
     * Switches between the light and dark themes.
     *
     * @param dark {@code true} for the dark theme
     */
    public void setDarkMode(boolean dark) {
        model.setDarkMode(dark);
    }

    /** Jumps the view to today's date. */
    public void goToday() {
        model.setViewDate(LocalDate.now());
    }

    /** Moves the view one step back, by an amount that depends on the view mode. */
    public void goPrevious() {
        LocalDate d = model.getCurrentViewDate();
        switch (model.getCurrentMode()) {
            case DAY:   model.setViewDate(d.minusDays(1));   break;
            case WEEK:  model.setViewDate(d.minusWeeks(1));  break;
            case MONTH: model.setViewDate(d.minusMonths(1)); break;
            case YEAR:  model.setViewDate(d.minusYears(1));  break;
        }
    }

    /** Moves the view one step forward, by an amount that depends on the view mode. */
    public void goNext() {
        LocalDate d = model.getCurrentViewDate();
        switch (model.getCurrentMode()) {
            case DAY:   model.setViewDate(model.nextDay(d));   break;
            case WEEK:  model.setViewDate(model.nextWeek(d));  break;
            case MONTH: model.setViewDate(model.nextMonth(d)); break;
            case YEAR:  model.setViewDate(model.nextYear(d));  break;
        }
    }

    /**
     * Loads the events from disk into the model.
     *
     * @throws StorageException if reading fails
     */
    public void loadFromDisk() throws StorageException {
        model.loadEvents(storage.load());
    }

    /**
     * Persists the current events to disk.
     *
     * @throws StorageException if writing fails
     */
    private void save() throws StorageException {
        storage.save(model.getAllEvents());
    }
}
