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
 * Encharged with saving and loading the events in a text file.
 * Each event ocupies one row, with the  fields separeted by TAB:
 * title, date, hour, duration, category, local, description,
 * reminder, repetition, exception-dates e participants.
 */
public class EventStorage {
    private final File file;

    public EventStorage(String fileName) {
        this.file = new File(fileName);
    }

    public void save(List<Event> events) throws StorageException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Event e : events) {
                writer.write(toLine(e));
                writer.newLine();
            }
        } catch (IOException ex) {
            throw new StorageException("It wasn't possible to save the events in the file.");
        }
    }

    /**
     * read the events from the file. If the file does not exist yet (first
     * execution), returns an empty list. Messed up lines are ignored
     * in order for corrupted files to not take down the program.
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
            throw new StorageException("It wasn't possible to read the events file.");
        }
        return events;
    }

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

        StringBuilder excepition_dates = new StringBuilder();
        for (LocalDate d : e.getExceptionDates()) {
            if (excepition_dates.length() > 0) {
                excepition_dates.append(',');
            }
            excepition_dates.append(d);
        }
        sb.append(excepition_dates).append('\t');

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
                for (String par : f[10].split(",")) {
                    String[] np = par.split("\\|", 2);
                    String nome = np[0];
                    String email = np.length > 1 ? np[1] : "";
                    e.addAttendee(new Attendee(nome, email));
                }
            }
            return e;
        } catch (RuntimeException ex) {
            // line out of expected format
            return null;
        }
    }

    // remove tabs and line breaks that would fuck up the format of the file
    private String clean(String value) {
        if (value == null) {
            return "";
        }
        return value.replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
    }

    // besides tabs, remove collons and bars, used as participants separator
    private String simple(String value) {
        return clean(value).replace(',', ' ').replace('|', ' ');
    }
}
