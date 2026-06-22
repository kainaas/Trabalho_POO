package Model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Persists events to (and loads them from) a plain text file.
 *
 * <p>Each event occupies one line, with TAB-separated fields: title, date, time,
 * duration, category, location, description, reminder, recurrence, exception
 * dates and attendees. Nested lists (attendees and exception dates) use a comma
 * as a secondary separator.</p>
 */
public class EventStorage {
    private final File file;

    /**
     * Creates a storage bound to the given file name.
     *
     * @param fileName path of the events file
     */
    public EventStorage(String fileName) {
        this.file = new File(fileName);
    }

    /**
     * Writes every event to the file, one per line.
     *
     * @param events the events to save
     * @throws StorageException if the file cannot be written
     */
    public void save(List<Event> events) throws StorageException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Event e : events) {
                writer.write(toLine(e));
                writer.newLine();
            }
        } catch (IOException ex) {
            throw new StorageException("It was not possible to save the events to the file.");
        }
    }

    /**
     * Reads the events from the file. If the file does not exist yet (first run),
     * an empty list is returned. Malformed lines are skipped so that a corrupted
     * file does not crash the program.
     *
     * @return the events read from disk
     * @throws StorageException if the file exists but cannot be read
     */
    public List<Event> load() throws StorageException {
        List<Event> events = new ArrayList<>();
        if (!file.exists()) {
            return events;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Event e = parseLine(line);
                if (e != null) {
                    events.add(e);
                }
            }
        } catch (IOException ex) {
            throw new StorageException("It was not possible to read the events file.");
        }
        return events;
    }

    /**
     * Serialises a single event into one storage line.
     *
     * @param e the event
     * @return the encoded line
     */
    private String toLine(Event e) {
        StringBuilder sb = new StringBuilder();
        sb.append(clean(e.getTitle())).append('\t');
        sb.append(e.getDate()).append('\t');
        sb.append(e.getTime()).append('\t');
        sb.append(e.getDuration()).append('\t');
        sb.append(clean(e.getCategory())).append('\t');
        sb.append(clean(e.getLocation())).append('\t');
        sb.append(clean(e.getDescription())).append('\t');
        sb.append(e.getReminderLeadMinutes()).append('\t');
        sb.append(e.getRecurrence().name()).append('\t');

        StringBuilder exceptionDates = new StringBuilder();
        for (LocalDate d : e.getExceptionDates()) {
            if (exceptionDates.length() > 0) {
                exceptionDates.append(',');
            }
            exceptionDates.append(d);
        }
        sb.append(exceptionDates).append('\t');

        StringBuilder invited = new StringBuilder();
        for (Attendee a : e.getAttendees()) {
            if (invited.length() > 0) {
                invited.append(',');
            }
            invited.append(simple(a.getName())).append('|').append(simple(a.getEmail()));
        }
        sb.append(invited);

        return sb.toString();
    }

    /**
     * Parses one storage line back into an event.
     *
     * @param line the encoded line
     * @return the event, or {@code null} if the line is malformed
     */
    private Event parseLine(String line) {
        String[] f = line.split("\t", -1);
        if (f.length < 11) {
            return null;
        }
        try {
            LocalDate date = LocalDate.parse(f[1]);
            LocalTime time = LocalTime.parse(f[2]);
            long duration = Long.parseLong(f[3]);

            Event e = new Event(
                date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                time.getHour(), time.getMinute(),
                duration, f[0], f[4], f[6]);
            e.setLocation(f[5]);
            e.setReminderLeadMinutes(Long.parseLong(f[7]));
            e.setRecurrence(Recurrence.valueOf(f[8]));

            if (!f[9].isEmpty()) {
                for (String d : f[9].split(",")) {
                    e.addExceptionDate(LocalDate.parse(d));
                }
            }
            if (!f[10].isEmpty()) {
                for (String guest : f[10].split(",")) {
                    String[] np = guest.split("\\|", 2);
                    String name = np[0];
                    String email = np.length > 1 ? np[1] : "";
                    e.addAttendee(new Attendee(name, email));
                }
            }
            return e;
        } catch (RuntimeException ex) {
            // line not in the expected format
            return null;
        }
    }

    /**
     * Removes tabs and line breaks that would corrupt the file format.
     *
     * @param value the raw text
     * @return the sanitised text
     */
    private String clean(String value) {
        if (value == null) {
            return "";
        }
        return value.replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
    }

    /**
     * Like {@link #clean(String)} but also removes the comma and pipe characters
     * used as attendee separators.
     *
     * @param value the raw text
     * @return the sanitised text
     */
    private String simple(String value) {
        return clean(value).replace(',', ' ').replace('|', ' ');
    }
}
