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
 * Defines an Event.
 */
public final class Event {
    private LocalDate date;
    private LocalTime time;
    private long duration; //in minutes
    private String title;
    private String location;
    private String description;
    private String category;

    // antecedence of reminder in minutes (0 = no reminder)
    private long reminderLeadMinutes;
    // type of repetitions
    private Recurrence recurrence;
    // invited participants
    private final List<Attendee> attendees = new ArrayList<>();
    // dates that are exceptions of recurrent events
    private final Set<LocalDate> exceptionDates = new HashSet<>();

    /**
     * Creates an event with all the information
     */
    public Event(int year, int month, int day, int hour, int minute, long duration, String title, String category, String description) {
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
     * Constructor without duration. default is an hour
     */
    public Event(int year, int month, int day, int hour, int minute, String title, String category, String description) {
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

    public void setTime(int hour, int minute) throws DateTimeException {
        //throws DateTimeException when hour < 0, hour > 23, minute < 0 or minute >59
        this.time = LocalTime.of(hour, minute, 0);
    }

    public void setDate(int year, int month, int day) throws DateTimeException {
        //throws DateTimeException when it's not a valid date
        this.date = LocalDate.of(year,month, day);
    }

    public void setTitle(String title) throws NullPointerException {
        this.title = title.trim();
    }

    public void setDurationInMinutes(long durationMinutes) {
        this.duration = durationMinutes;
    }

    public void setDurationInHours(long hours, long minutes) {
        this.duration = hours*60 + minutes;
    }

    public void setLocation(String location) throws NullPointerException {
        this.location = location;
    }

    public void setCategory(String category) throws NullPointerException {
        this.category = category;
    }

    public void setDescription(String description) throws NullPointerException {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public long getDuration() {
        return duration;
    }

    public String getLocation() {
        return location;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getEndOfevent() {
        LocalDateTime end = LocalDateTime.of(this.date, this.time);
        return end.plusMinutes(this.duration);
    }

    public void setReminderLeadMinutes(long minutes) {
        this.reminderLeadMinutes = minutes;
    }

    public long getReminderLeadMinutes() {
        return reminderLeadMinutes;
    }

    public void setRecurrence(Recurrence recurrence) {
        this.recurrence = recurrence;
    }

    public Recurrence getRecurrence() {
        return recurrence;
    }

    public void addAttendee(Attendee attendee) {
        attendees.add(attendee);
    }

    public List<Attendee> getAttendees() {
        return attendees;
    }

    public void clearAttendees() {
        attendees.clear();
    }

    // marks a date as an exception
    public void addExceptionDate(LocalDate date) {
        exceptionDates.add(date);
    }

    public Set<LocalDate> getExceptionDates() {
        return exceptionDates;
    }

    public boolean isRecurring() {
        return recurrence != Recurrence.NONE;
    }

    /**
     * tells if an event occurs in a designated day, already considering repetition
     * and the exception dates.
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

    // exact moment (date + hour) that the ocurrence of that day starts
    public LocalDateTime occurrenceStart(LocalDate day) {
        return LocalDateTime.of(day, time);
    }
}