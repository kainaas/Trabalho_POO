import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import Model.Attendee;
import Model.Event;
import Model.EventStorage;
import Model.Recurrence;
import Model.StorageException;

/**
 * Unit tests for {@link EventStorage}: the save/load round-trip, tolerance to
 * malformed lines and the empty result for a missing file.
 */
public class EventStorageTest {

    @TempDir
    Path tempDir;

    private EventStorage storage;
    private Path file;

    @BeforeEach
    void setUp() {
        file = tempDir.resolve("events.txt");
        storage = new EventStorage(file.toString());
    }

    @Test
    void loadOnMissingFileReturnsEmpty() throws StorageException {
        assertTrue(storage.load().isEmpty());
    }

    @Test
    void saveThenLoadRoundTrip() throws StorageException {
        Event e = new Event(2026, 6, 10, 9, 30, 90, "Roundtrip", "Meeting", "details");
        e.setLocation("Room 2");
        e.setRecurrence(Recurrence.WEEKLY);
        e.setReminderLeadMinutes(60);
        e.addAttendee(new Attendee("Ana", "ana@example.com"));

        List<Event> toSave = new ArrayList<>();
        toSave.add(e);
        storage.save(toSave);

        List<Event> loaded = storage.load();
        assertEquals(1, loaded.size());
        Event r = loaded.get(0);
        assertEquals("Roundtrip", r.getTitle());
        assertEquals(90, r.getDuration());
        assertEquals("Room 2", r.getLocation());
        assertEquals(Recurrence.WEEKLY, r.getRecurrence());
        assertEquals(60, r.getReminderLeadMinutes());
        assertEquals(1, r.getAttendees().size());
        assertEquals("Ana", r.getAttendees().get(0).getName());
    }

    @Test
    void malformedLinesAreIgnored() throws Exception {
        Event e = new Event(2026, 6, 10, 9, 0, 60, "Good", "Meeting", "");
        List<Event> toSave = new ArrayList<>();
        toSave.add(e);
        storage.save(toSave);

        // Append a corrupted line; it must not break loading.
        Files.write(file, "this\tis\tbroken\n".getBytes(),
            java.nio.file.StandardOpenOption.APPEND);

        List<Event> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("Good", loaded.get(0).getTitle());
    }

    @Test
    void exceptionDatesSurviveRoundTrip() throws StorageException {
        Event e = new Event(2026, 6, 10, 9, 0, 60, "Daily", "Meeting", "");
        e.setRecurrence(Recurrence.DAILY);
        e.addExceptionDate(LocalDate.of(2026, 6, 12));

        List<Event> toSave = new ArrayList<>();
        toSave.add(e);
        storage.save(toSave);

        Event r = storage.load().get(0);
        assertTrue(r.getExceptionDates().contains(LocalDate.of(2026, 6, 12)));
    }
}
