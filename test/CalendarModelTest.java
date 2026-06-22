import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Model.CalendarModel;
import Model.Event;
import Model.Recurrence;

/**
 * Unit tests for {@link CalendarModel}: duplicate-title rules, counting,
 * searching and the chronological ordering of a day's events.
 */
public class CalendarModelTest {

    private CalendarModel model;

    @BeforeEach
    void setUp() {
        model = new CalendarModel();
    }

    private Event event(String title, int day, int hour) {
        return new Event(2026, 6, day, hour, 0, 60, title, "Meeting", "");
    }

    @Test
    void addsEventWithUniqueTitle() {
        assertTrue(model.addEvent(event("Alpha", 10, 9)));
        assertEquals(1, model.getAllEvents().size());
    }

    @Test
    void rejectsDuplicateTitleCaseInsensitive() {
        assertTrue(model.addEvent(event("Alpha", 10, 9)));
        assertFalse(model.addEvent(event("alpha", 11, 9)));
        assertEquals(1, model.getAllEvents().size());
    }

    @Test
    void countsAndDetectsEventsOnADay() {
        model.addEvent(event("Alpha", 10, 9));
        model.addEvent(event("Beta", 10, 8));
        assertEquals(2, model.countEventsOn(LocalDate.of(2026, 6, 10)));
        assertTrue(model.hasEventsOn(LocalDate.of(2026, 6, 10)));
        assertFalse(model.hasEventsOn(LocalDate.of(2026, 6, 12)));
    }

    @Test
    void getEventsOnIsSortedByTime() {
        model.addEvent(event("Late", 10, 15));
        model.addEvent(event("Early", 10, 8));
        List<Event> ofDay = model.getEventsOn(LocalDate.of(2026, 6, 10));
        assertEquals("Early", ofDay.get(0).getTitle());
        assertEquals("Late", ofDay.get(1).getTitle());
    }

    @Test
    void searchMatchesTitleAndCategory() {
        model.addEvent(event("Dentist", 10, 9));
        assertEquals(1, model.searchEvents("dent").size());
        assertEquals(1, model.searchEvents("Meeting").size());
        assertTrue(model.searchEvents("nothing").isEmpty());
    }

    @Test
    void removeOccurrenceKeepsTheSeries() {
        Event recurring = event("Daily", 10, 9);
        recurring.setRecurrence(Recurrence.DAILY);
        model.addEvent(recurring);
        model.removeOccurrence(recurring, LocalDate.of(2026, 6, 11));
        assertFalse(model.hasEventsOn(LocalDate.of(2026, 6, 11)));
        assertTrue(model.hasEventsOn(LocalDate.of(2026, 6, 12)));
    }

    @Test
    void addEventForcedBypassesTitleCheck() {
        model.addEvent(event("Same", 10, 9));
        model.addEventForced(event("Same", 11, 9));
        assertEquals(2, model.getAllEvents().size());
    }
}
