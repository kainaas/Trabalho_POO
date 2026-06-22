import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import Model.Event;
import Model.Recurrence;

/**
 * Unit tests for {@link Event}, focusing on the {@code occursOn} logic for the
 * different recurrence rules and on the exception dates.
 */
public class EventTest {

    private Event sample(Recurrence r) {
        // Wednesday, 2026-06-10, 09:00, 60 min
        Event e = new Event(2026, 6, 10, 9, 0, 60, "Standup", "Meeting", "daily sync");
        e.setRecurrence(r);
        return e;
    }

    @Test
    void nonRecurringOccursOnlyOnItsDate() {
        Event e = sample(Recurrence.NONE);
        assertTrue(e.occursOn(LocalDate.of(2026, 6, 10)));
        assertFalse(e.occursOn(LocalDate.of(2026, 6, 11)));
    }

    @Test
    void neverOccursBeforeItsStart() {
        Event e = sample(Recurrence.DAILY);
        assertFalse(e.occursOn(LocalDate.of(2026, 6, 9)));
    }

    @Test
    void dailyOccursEveryDayFromStart() {
        Event e = sample(Recurrence.DAILY);
        assertTrue(e.occursOn(LocalDate.of(2026, 6, 10)));
        assertTrue(e.occursOn(LocalDate.of(2026, 6, 11)));
        assertTrue(e.occursOn(LocalDate.of(2026, 7, 1)));
    }

    @Test
    void weeklyOccursOnSameWeekday() {
        Event e = sample(Recurrence.WEEKLY); // Wednesday
        assertTrue(e.occursOn(LocalDate.of(2026, 6, 17)));  // next Wednesday
        assertFalse(e.occursOn(LocalDate.of(2026, 6, 16))); // Tuesday
    }

    @Test
    void monthlyOccursOnSameDayOfMonth() {
        Event e = sample(Recurrence.MONTHLY);
        assertTrue(e.occursOn(LocalDate.of(2026, 7, 10)));
        assertFalse(e.occursOn(LocalDate.of(2026, 7, 11)));
    }

    @Test
    void exceptionDateIsSkipped() {
        Event e = sample(Recurrence.DAILY);
        e.addExceptionDate(LocalDate.of(2026, 6, 11));
        assertFalse(e.occursOn(LocalDate.of(2026, 6, 11)));
        assertTrue(e.occursOn(LocalDate.of(2026, 6, 12)));
    }

    @Test
    void endOfEventAddsDuration() {
        Event e = sample(Recurrence.NONE);
        assertEquals(LocalDate.of(2026, 6, 10).atTime(10, 0), e.getEndOfEvent());
    }

    @Test
    void isRecurringReflectsRule() {
        assertFalse(sample(Recurrence.NONE).isRecurring());
        assertTrue(sample(Recurrence.WEEKLY).isRecurring());
    }
}
