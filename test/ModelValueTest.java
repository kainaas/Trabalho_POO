import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import Model.Attendee;
import Model.Recurrence;

/**
 * Small value-object tests for {@link Attendee} and {@link Recurrence}.
 */
public class ModelValueTest {

    @Test
    void attendeeToStringWithEmail() {
        assertEquals("Ana <ana@x.com>", new Attendee("Ana", "ana@x.com").toString());
    }

    @Test
    void attendeeToStringWithoutEmail() {
        assertEquals("Bob", new Attendee("Bob", "").toString());
    }

    @Test
    void recurrenceLabels() {
        assertEquals("Does not repeat", Recurrence.NONE.toString());
        assertEquals("Daily", Recurrence.DAILY.toString());
        assertEquals("Weekly", Recurrence.WEEKLY.toString());
        assertEquals("Monthly", Recurrence.MONTHLY.toString());
    }
}
