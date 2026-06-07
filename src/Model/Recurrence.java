package Model;

/**
 * Tipos de repeticao que um evento pode ter.
 */
public enum Recurrence {
    NONE, DAILY, WEEKLY, MONTHLY;

    @Override
    public String toString() {
        switch (this) {
            case DAILY:   return "Diario";
            case WEEKLY:  return "Semanal";
            case MONTHLY: return "Mensal";
            default:      return "Nao se repete";
        }
    }
}
