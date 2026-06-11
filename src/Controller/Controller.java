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
 * Connects the interface to the model. Make the data validation, creates/edits/erases
 * events and tells to save in the file. Does not have direct access to screen elements:
 * who notifies the view are the notifications from the model.
 */
public class Controller {
    private final CalendarModel model;
    private final EventStorage storage;

    public Controller(CalendarModel model, EventStorage storage) {
        this.model = model;
        this.storage = storage;
    }

    public CalendarModel getModel() {
        return model;
    }

    /**
     * Builds an event from the formulary data, validating everything.
     * Throws EventValidationException with a message when an error happens
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
                "Date or time invalid. Check the day, month and time");
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

    // Adds a new event; Returns false if there's already an event with this title
    public boolean createEvent(Event e) throws StorageException {
        boolean adicionado = model.addEvent(e);
        if (adicionado) {
            save();
        }
        return adicionado;
    }

    /** 
     * Changes the event for an edited version
     * @returns false if the new title already belongs to other events (in that case, keeps the old one)
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

    // Edits only the ocurrence of that day: takes it out of the recurrence and creates a new event
    public void editOccurrence(Event master, LocalDate day, Event edited) throws StorageException {
        master.addExceptionDate(day);
        edited.setRecurrence(Recurrence.NONE);
        model.addEventForced(edited);
        save();
    }

    public void deleteEvent(Event e) throws StorageException {
        model.removeEvent(e);
        save();
    }

    // apaga so a ocorrencia daquele dia, mantendo o resto da serie
    public void deleteOccurrence(Event master, LocalDate day) throws StorageException {
        model.removeOccurrence(master, day);
        save();
    }

    public List<Event> search(String keyword) {
        return model.searchEvents(keyword);
    }

    public void selectDate(LocalDate day) {
        model.setViewDate(day);
        model.setEventSelected(null);
    }

    public void setViewMode(ViewMode mode) {
        model.setViewMode(mode);
    }

    public void setDarkMode(boolean dark) {
        model.setDarkMode(dark);
    }

    public void goToday() {
        model.setViewDate(LocalDate.now());
    }

    public void goPrevious() {
        LocalDate d = model.getCurrentViewDate();
        switch (model.getCurrentMode()) {
            case DAY:   model.setViewDate(d.minusDays(1));   break;
            case WEEK:  model.setViewDate(d.minusWeeks(1));  break;
            case MONTH: model.setViewDate(d.minusMonths(1)); break;
            case YEAR:  model.setViewDate(d.minusYears(1));  break;
        }
    }

    public void goNext() {
        LocalDate d = model.getCurrentViewDate();
        switch (model.getCurrentMode()) {
            case DAY:   model.setViewDate(model.nextDay(d));   break;
            case WEEK:  model.setViewDate(model.nextWeek(d));  break;
            case MONTH: model.setViewDate(model.nextMonth(d)); break;
            case YEAR:  model.setViewDate(model.nextYear(d));  break;
        }
    }

    public void loadFromDisk() throws StorageException {
        model.loadEvents(storage.load());
    }

    private void save() throws StorageException {
        storage.save(model.getAllEvents());
    }
}
