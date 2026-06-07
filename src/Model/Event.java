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
    private String category; //TODO: create some form of structure that adds categories to the system

    // antecedencia do lembrete em minutos (0 = sem lembrete)
    private long reminderLeadMinutes;
    // tipo de repeticao do evento
    private Recurrence recurrence;
    // participantes convidados
    private final List<Attendee> attendees = new ArrayList<>();
    // datas que foram tiradas de uma serie recorrente (ocorrencias apagadas/editadas)
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

    // marca uma data como excecao, para que a serie nao apareca mais nesse dia
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
     * Diz se o evento acontece no dia informado, ja considerando a repeticao
     * e as datas que foram retiradas da serie.
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

    // momento exato (data + hora) em que a ocorrencia daquele dia comeca
    public LocalDateTime occurrenceStart(LocalDate day) {
        return LocalDateTime.of(day, time);
    }
}