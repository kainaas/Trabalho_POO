import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import Controller.Controller;
import Model.CalendarModel;
import Model.Event;
import Model.EventStorage;
import Model.EventValidationException;
import Model.Recurrence;
import Model.StorageException;

/**
 * Unit tests for {@link Controller}: input validation in {@code buildEvent} and
 * the create/replace operations (each backed by a temporary storage file).
 */
public class ControllerTest {

    @TempDir
    Path tempDir;

    private Controller controller;
    private CalendarModel model;

    @BeforeEach
    void setUp() {
        model = new CalendarModel();
        EventStorage storage = new EventStorage(tempDir.resolve("events.txt").toString());
        controller = new Controller(model, storage);
    }

    @Test
    void buildEventRejectsEmptyTitle() {
        assertThrows(EventValidationException.class, () ->
            controller.buildEvent(2026, 6, 10, 9, 0, 60, "  ", "Meeting", "", "",
                0, Recurrence.NONE, new ArrayList<>()));
    }

    @Test
    void buildEventRejectsNonPositiveDuration() {
        assertThrows(EventValidationException.class, () ->
            controller.buildEvent(2026, 6, 10, 9, 0, 0, "Title", "Meeting", "", "",
                0, Recurrence.NONE, new ArrayList<>()));
    }

    @Test
    void buildEventRejectsInvalidDate() {
        assertThrows(EventValidationException.class, () ->
            controller.buildEvent(2026, 2, 30, 9, 0, 60, "Title", "Meeting", "", "",
                0, Recurrence.NONE, new ArrayList<>()));
    }

    @Test
    void buildEventAcceptsValidData() throws EventValidationException {
        Event e = controller.buildEvent(2026, 6, 10, 9, 0, 60, "Valid", "Meeting",
            "Room 1", "desc", 10, Recurrence.WEEKLY, new ArrayList<>());
        assertNotNull(e);
        assertEquals("Valid", e.getTitle());
        assertEquals(Recurrence.WEEKLY, e.getRecurrence());
    }

    @Test
    void createEventRejectsDuplicate() throws StorageException, EventValidationException {
        Event a = controller.buildEvent(2026, 6, 10, 9, 0, 60, "Dup", "Meeting", "", "",
            0, Recurrence.NONE, new ArrayList<>());
        Event b = controller.buildEvent(2026, 6, 11, 9, 0, 60, "Dup", "Meeting", "", "",
            0, Recurrence.NONE, new ArrayList<>());
        assertTrue(controller.createEvent(a));
        assertFalse(controller.createEvent(b));
        assertEquals(1, model.getAllEvents().size());
    }

    @Test
    void createEventWritesToDisk() throws Exception {
        Event a = controller.buildEvent(2026, 6, 10, 9, 0, 60, "Persisted", "Meeting", "", "",
            0, Recurrence.NONE, new ArrayList<>());
        controller.createEvent(a);
        Path file = tempDir.resolve("events.txt");
        assertTrue(Files.exists(file));
        assertTrue(readAll(file).contains("Persisted"));
    }

    private String readAll(Path file) throws IOException {
        return new String(Files.readAllBytes(file));
    }
}
