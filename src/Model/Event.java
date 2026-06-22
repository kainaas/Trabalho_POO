package Model;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a single calendar event.
 *
 * <p>An event has a starting date and time, a duration, a title, a category, a
 * location and a free-text description. It may also repeat (see {@link Recurrence})
 * and carry a list of {@link Attendee attendees} and a set of exception dates
 * (occurrences that were individually removed from a recurring series).</p>
 */
public final class Event {
    private LocalDate date;
    private LocalTime time;
    private long duration; // in minutes
    private String title;
    private String location;
    private String description;
    private String category;

    /** How long before the event the reminder fires, in minutes (0 = no reminder). */
    private long reminderLeadMinutes;
    /** Repetition rule of the event. */
    private Recurrence recurrence;
    /** Guests invited to the event. */
    private final List<Attendee> attendees = new ArrayList<>();
    /** Dates that were excluded from a recurring series. */
    private final Set<LocalDate> exceptionDates = new HashSet<>();

    /**
     * Creates an event with all of its data.
     *
     * @param year        starting year
     * @param month       starting month (1-12)
     * @param day         starting day of month
     * @param hour        starting hour (0-23)
     * @param minute      starting minute (0-59)
     * @param duration    duration in minutes
     * @param title       event title
     * @param category    event category
     * @param description free-text description
     * @throws DateTimeException if the date or time is invalid
     */
    public Event(int year, int month, int day, int hour, int minute, long duration,
            String title, String category, String description) {
        setTime(hour, minute);
        setDate(year, month, day);
        setDurationInMinutes(duration);
        setTitle(title);
        setCategory(category);
        setDescription(description);
        this.location = "";
        this.recurrence = Recurrence.NONE;
        this.reminderLeadMinutes = 0;
    }

    /**
     * Creates an event without an explicit duration; the duration defaults to one hour.
     *
     * @param year        starting year
     * @param month       starting month (1-12)
     * @param day         starting day of month
     * @param hour        starting hour (0-23)
     * @param minute      starting minute (0-59)
     * @param title       event title
     * @param category    event category
     * @param description free-text description
     * @throws DateTimeException if the date or time is invalid
     */
    public Event(int year, int month, int day, int hour, int minute,
            String title, String category, String description) {
        setTime(hour, minute);
        setDate(year, month, day);
        setTitle(title);
        setCategory(category);
        setDescription(description);
        this.duration = 60;
        this.location = "";
        this.recurrence = Recurrence.NONE;
        this.reminderLeadMinutes = 0;
    }

    /**
     * Sets the event time.
     *
     * @param hour   hour of day (0-23)
     * @param minute minute (0-59)
     * @throws DateTimeException if the hour or minute is out of range
     */
    public void setTime(int hour, int minute) throws DateTimeException {
        this.time = LocalTime.of(hour, minute, 0);
    }

    /**
     * Sets the event date.
     *
     * @param year  year
     * @param month month (1-12)
     * @param day   day of month
     * @throws DateTimeException if the date does not exist
     */
    public void setDate(int year, int month, int day) throws DateTimeException {
        this.date = LocalDate.of(year, month, day);
    }

    /**
     * Sets the event title (trimmed).
     *
     * @param title the title
     * @throws NullPointerException if {@code title} is {@code null}
     */
    public void setTitle(String title) throws NullPointerException {
        this.title = title.trim();
    }

    /**
     * Sets the duration in minutes.
     *
     * @param durationMinutes duration in minutes
     */
    public void setDurationInMinutes(long durationMinutes) {
        this.duration = durationMinutes;
    }

    /**
     * Sets the duration from hours and minutes.
     *
     * @param hours   hours
     * @param minutes minutes
     */
    public void setDurationInHours(long hours, long minutes) {
        this.duration = hours * 60 + minutes;
    }

    /**
     * Sets the event location.
     *
     * @param location the location
     * @throws NullPointerException if {@code location} is {@code null}
     */
    public void setLocation(String location) throws NullPointerException {
        this.location = location;
    }

    /**
     * Sets the event category.
     *
     * @param category the category
     * @throws NullPointerException if {@code category} is {@code null}
     */
    public void setCategory(String category) throws NullPointerException {
        this.category = category;
    }

    /**
     * Sets the event description.
     *
     * @param description the description
     * @throws NullPointerException if {@code description} is {@code null}
     */
    public void setDescription(String description) throws NullPointerException {
        this.description = description;
    }

    /** @return the event category */
    public String getCategory() {
        return category;
    }

    /** @return the event description */
    public String getDescription() {
        return description;
    }

    /** @return the starting date */
    public LocalDate getDate() {
        return date;
    }

    /** @return the duration in minutes */
    public long getDuration() {
        return duration;
    }

    /** @return the event location */
    public String getLocation() {
        return location;
    }

    /** @return the starting time */
    public LocalTime getTime() {
        return time;
    }

    /** @return the event title */
    public String getTitle() {
        return title;
    }

    /**
     * Computes the moment the event ends, from its start plus its duration.
     *
     * @return the end date-time of the event
     */
    public LocalDateTime getEndOfEvent() {
        LocalDateTime end = LocalDateTime.of(this.date, this.time);
        return end.plusMinutes(this.duration);
    }

    /**
     * Sets the reminder lead time.
     *
     * @param minutes minutes before the start (0 = no reminder)
     */
    public void setReminderLeadMinutes(long minutes) {
        this.reminderLeadMinutes = minutes;
    }

    /** @return minutes before the start at which the reminder fires (0 = none) */
    public long getReminderLeadMinutes() {
        return reminderLeadMinutes;
    }

    /**
     * Sets the recurrence rule.
     *
     * @param recurrence the recurrence rule
     */
    public void setRecurrence(Recurrence recurrence) {
        this.recurrence = recurrence;
    }

    /** @return the recurrence rule */
    public Recurrence getRecurrence() {
        return recurrence;
    }

    /**
     * Adds a guest to the event.
     *
     * @param attendee the attendee to add
     */
    public void addAttendee(Attendee attendee) {
        attendees.add(attendee);
    }

    /** @return the list of attendees */
    public List<Attendee> getAttendees() {
        return attendees;
    }

    /** Removes every attendee from the event. */
    public void clearAttendees() {
        attendees.clear();
    }

    /**
     * Marks a date as an exception, removing a single occurrence from a series.
     *
     * @param date the date to exclude
     */
    public void addExceptionDate(LocalDate date) {
        exceptionDates.add(date);
    }

    /** @return the set of excluded dates */
    public Set<LocalDate> getExceptionDates() {
        return exceptionDates;
    }

    /** @return {@code true} if the event repeats */
    public boolean isRecurring() {
        return recurrence != Recurrence.NONE;
    }

    /**
     * Tells whether the event takes place on the given day, taking the recurrence
     * rule and the exception dates into account.
     *
     * @param day the day to test
     * @return {@code true} if an occurrence falls on {@code day}
     */
    public boolean occursOn(LocalDate day) {
        if (day.isBefore(date) || exceptionDates.contains(day)) {
            return false;
        }
        switch (recurrence) {
            case DAILY:   return true;
            case WEEKLY:  return day.getDayOfWeek() == date.getDayOfWeek();
            case MONTHLY: return day.getDayOfMonth() == date.getDayOfMonth();
            default:      return day.equals(date);
        }
    }

    /**
     * Exact moment (date + time) at which the occurrence on the given day starts.
     *
     * @param day the day of the occurrence
     * @return the start date-time of that occurrence
     */
    public LocalDateTime occurrenceStart(LocalDate day) {
        return LocalDateTime.of(day, time);
    }
}
