package Model;

/**
 * Types of repetition an event may have
 */
public enum Recurrence {
    NONE, DAILY, WEEKLY, MONTHLY;

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
