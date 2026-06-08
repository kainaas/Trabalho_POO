package Model;

/**
 * Problema ao ler ou gravar o arquivo de eventos.
 * A mensagem ja vem pronta para ser mostrada ao usuario.
 */
public class StorageException extends Exception {
    public StorageException(String message) {
        super(message);
    }
}
