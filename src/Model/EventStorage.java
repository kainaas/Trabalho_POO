package Model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Cuida de salvar e carregar os eventos num arquivo de texto.
 * Cada evento ocupa uma linha, com os campos separados por TAB:
 * titulo, data, hora, duracao, categoria, local, descricao,
 * lembrete, repeticao, datas-excecao e participantes.
 */
public class EventStorage {
    private final File file;

    public EventStorage(String fileName) {
        this.file = new File(fileName);
    }

    public void save(List<Event> events) throws StorageException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Event e : events) {
                writer.write(toLine(e));
                writer.newLine();
            }
        } catch (IOException ex) {
            throw new StorageException("Nao foi possivel salvar os eventos no arquivo.");
        }
    }

    /**
     * Le os eventos do arquivo. Se o arquivo ainda nao existe (primeira
     * execucao) devolve uma lista vazia. Linhas estragadas sao ignoradas
     * para que um arquivo corrompido nao derrube o programa.
     */
    public List<Event> load() throws StorageException {
        List<Event> events = new ArrayList<>();
        if (!file.exists()) {
            return events;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Event e = parseLine(line);
                if (e != null) {
                    events.add(e);
                }
            }
        } catch (IOException ex) {
            throw new StorageException("Nao foi possivel ler o arquivo de eventos.");
        }
        return events;
    }

    private String toLine(Event e) {
        StringBuilder sb = new StringBuilder();
        sb.append(clean(e.getTitle())).append('\t');
        sb.append(e.getDate()).append('\t');
        sb.append(e.getTime()).append('\t');
        sb.append(e.getDuration()).append('\t');
        sb.append(clean(e.getCategory())).append('\t');
        sb.append(clean(e.getLocation())).append('\t');
        sb.append(clean(e.getDescription())).append('\t');
        sb.append(e.getReminderLeadMinutes()).append('\t');
        sb.append(e.getRecurrence().name()).append('\t');

        StringBuilder excecoes = new StringBuilder();
        for (LocalDate d : e.getExceptionDates()) {
            if (excecoes.length() > 0) {
                excecoes.append(',');
            }
            excecoes.append(d);
        }
        sb.append(excecoes).append('\t');

        StringBuilder convidados = new StringBuilder();
        for (Attendee a : e.getAttendees()) {
            if (convidados.length() > 0) {
                convidados.append(',');
            }
            convidados.append(simple(a.getName())).append('|').append(simple(a.getEmail()));
        }
        sb.append(convidados);

        return sb.toString();
    }

    private Event parseLine(String line) {
        String[] f = line.split("\t", -1);
        if (f.length < 11) {
            return null;
        }
        try {
            LocalDate date = LocalDate.parse(f[1]);
            LocalTime time = LocalTime.parse(f[2]);
            long duration = Long.parseLong(f[3]);

            Event e = new Event(
                date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                time.getHour(), time.getMinute(),
                duration, f[0], f[4], f[6]);
            e.setLocation(f[5]);
            e.setReminderLeadMinutes(Long.parseLong(f[7]));
            e.setRecurrence(Recurrence.valueOf(f[8]));

            if (!f[9].isEmpty()) {
                for (String d : f[9].split(",")) {
                    e.addExceptionDate(LocalDate.parse(d));
                }
            }
            if (!f[10].isEmpty()) {
                for (String par : f[10].split(",")) {
                    String[] np = par.split("\\|", 2);
                    String nome = np[0];
                    String email = np.length > 1 ? np[1] : "";
                    e.addAttendee(new Attendee(nome, email));
                }
            }
            return e;
        } catch (RuntimeException ex) {
            // linha fora do formato esperado, ignora e segue
            return null;
        }
    }

    // tira tabs e quebras de linha que estragariam o formato do arquivo
    private String clean(String value) {
        if (value == null) {
            return "";
        }
        return value.replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
    }

    // alem de tabs, tira virgula e barra usadas como separadores de participante
    private String simple(String value) {
        return clean(value).replace(',', ' ').replace('|', ' ');
    }
}
