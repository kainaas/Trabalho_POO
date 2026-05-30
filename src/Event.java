import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
    private String category; //TODO: create some form of structure that adds categories to the system

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
        this.title = title;
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
}