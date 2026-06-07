package Model;

/**
 * Lancada quando os dados informados para um evento sao invalidos.
 * A mensagem ja vem pronta para ser mostrada ao usuario.
 */
public class EventValidationException extends Exception {
    public EventValidationException(String message) {
        super(message);
    }
}
