package View;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import Model.CalendarModel;
import Model.Event;

/**
 * Fica rodando numa thread separada e, de tempos em tempos, confere se algum
 * evento esta chegando perto do seu horario de lembrete. Quando chega a hora,
 * mostra um aviso na tela. Cada lembrete so aparece uma vez.
 */
public class ReminderService implements Runnable {
    private static final long INTERVALO_MS = 30_000; // confere a cada 30 segundos

    private final CalendarModel model;
    private final Set<String> jaAvisados = new HashSet<>();
    private Thread thread;

    public ReminderService(CalendarModel model) {
        this.model = model;
    }

    public void start() {
        thread = new Thread(this, "reminder-service");
        thread.setDaemon(true); // nao segura o programa ao fechar a janela
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            checkReminders();
            try {
                Thread.sleep(INTERVALO_MS);
            } catch (InterruptedException ex) {
                return;
            }
        }
    }

    private void checkReminders() {
        LocalDateTime agora = LocalDateTime.now();

        for (Event e : model.getAllEvents()) {
            long lead = e.getReminderLeadMinutes();
            if (lead <= 0) {
                continue;
            }
            // olha as ocorrencias de hoje e de amanha
            for (int i = 0; i <= 1; i++) {
                LocalDate dia = agora.toLocalDate().plusDays(i);
                if (!e.occursOn(dia)) {
                    continue;
                }
                LocalDateTime inicio = e.occurrenceStart(dia);
                LocalDateTime aviso = inicio.minusMinutes(lead);

                // ja passou da hora do lembrete mas o evento ainda nao comecou
                if (!agora.isBefore(aviso) && agora.isBefore(inicio)) {
                    String chave = e.getTitle() + "@" + inicio;
                    if (jaAvisados.add(chave)) {
                        mostrarAviso(e, inicio);
                    }
                }
            }
        }
    }

    private void mostrarAviso(Event e, LocalDateTime inicio) {
        String mensagem = "Lembrete: \"" + e.getTitle() + "\"\n"
            + "Comeca as " + e.getTime()
            + " (" + e.getCategory() + ")";
        // mostra o aviso na thread da interface, que e o jeito certo no Swing
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(null, mensagem, "Lembrete de evento",
                JOptionPane.INFORMATION_MESSAGE));
    }
}
