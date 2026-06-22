package Model;

/**
 * Repetition rule that an event may follow.
 */
public enum Recurrence {
    /** The event happens only once. */
    NONE,
    /** The event repeats every day. */
    DAILY,
    /** The event repeats on the same weekday every week. */
    WEEKLY,
    /** The event repeats on the same day-of-month every month. */
    MONTHLY;

    /**
     * @return a human-readable label for the rule
     */
    @Override
    public String toString() {
        switch (this) {
            case DAILY:   return "Daily";
            case WEEKLY:  return "Weekly";
            case MONTHLY: return "Monthly";
            default:      return "Does not repeat";
        }
    }
}
