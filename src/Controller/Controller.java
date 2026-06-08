package Controller;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;

import Model.Attendee;
import Model.CalendarModel;
import Model.Event;
import Model.EventStorage;
import Model.EventValidationException;
import Model.Recurrence;
import Model.StorageException;
import View.ViewMode;

/**
 * Liga a interface ao modelo. Faz a validacao dos dados, cria/edita/apaga
 * eventos e manda salvar no arquivo. Nao mexe diretamente em componentes
 * de tela: quem avisa a view sao as notificacoes do proprio modelo.
 */
public class Controller {
    private final CalendarModel model;
    private final EventStorage storage;

    public Controller(CalendarModel model, EventStorage storage) {
        this.model = model;
        this.storage = storage;
    }

    public CalendarModel getModel() {
        return model;
    }

    /**
     * Monta um evento a partir dos dados do formulario, validando tudo.
     * Em caso de erro lanca EventValidationException com uma mensagem
     * ja pronta para o usuario.
     */
    public Event buildEvent(int year, int month, int day, int hour, int minute,
            long duration, String title, String category, String location,
            String description, long reminderLead, Recurrence recurrence,
            List<Attendee> attendees) throws EventValidationException {

        if (title == null || title.trim().isEmpty()) {
            throw new EventValidationException("O titulo do evento nao pode ficar vazio.");
        }
        if (duration <= 0) {
            throw new EventValidationException("A duracao deve ser de pelo menos 1 minuto.");
        }

        Event e;
        try {
            e = new Event(year, month, day, hour, minute,
                    duration, title, category, description);
        } catch (DateTimeException ex) {
            throw new EventValidationException(
                "Data ou hora invalida. Confira o dia, o mes e o horario.");
        }

        e.setLocation(location == null ? "" : location.trim());
        e.setReminderLeadMinutes(reminderLead);
        e.setRecurrence(recurrence);
        if (attendees != null) {
            for (Attendee a : attendees) {
                e.addAttendee(a);
            }
        }
        return e;
    }

    // adiciona um evento novo; devolve false se ja existe outro com o mesmo titulo
    public boolean createEvent(Event e) throws StorageException {
        boolean adicionado = model.addEvent(e);
        if (adicionado) {
            save();
        }
        return adicionado;
    }

    // troca um evento por uma versao editada (usado quando o usuario edita a serie toda).
    // devolve false se o novo titulo bater com outro evento ja existente (ai mantem o antigo)
    public boolean replaceEvent(Event oldEvent, Event newEvent) throws StorageException {
        model.removeEvent(oldEvent);
        boolean ok = model.addEvent(newEvent);
        if (!ok) {
            model.addEvent(oldEvent); // desfaz para nao perder o evento antigo
            return false;
        }
        save();
        return true;
    }

    // edita apenas a ocorrencia daquele dia: tira ela da serie e cria um evento avulso
    public void editOccurrence(Event master, LocalDate day, Event edited) throws StorageException {
        master.addExceptionDate(day);
        edited.setRecurrence(Recurrence.NONE);
        model.addEventForced(edited);
        save();
    }

    public void deleteEvent(Event e) throws StorageException {
        model.removeEvent(e);
        save();
    }

    // apaga so a ocorrencia daquele dia, mantendo o resto da serie
    public void deleteOccurrence(Event master, LocalDate day) throws StorageException {
        model.removeOccurrence(master, day);
        save();
    }

    public List<Event> search(String keyword) {
        return model.searchEvents(keyword);
    }

    public void selectDate(LocalDate day) {
        model.setViewDate(day);
        model.setEventSelected(null);
    }

    public void setViewMode(ViewMode mode) {
        model.setViewMode(mode);
    }

    public void setDarkMode(boolean dark) {
        model.setDarkMode(dark);
    }

    public void goToday() {
        model.setViewDate(LocalDate.now());
    }

    public void goPrevious() {
        LocalDate d = model.getCurrentViewDate();
        switch (model.getCurrentMode()) {
            case DAY:   model.setViewDate(d.minusDays(1));   break;
            case WEEK:  model.setViewDate(d.minusWeeks(1));  break;
            case MONTH: model.setViewDate(d.minusMonths(1)); break;
            case YEAR:  model.setViewDate(d.minusYears(1));  break;
        }
    }

    public void goNext() {
        LocalDate d = model.getCurrentViewDate();
        switch (model.getCurrentMode()) {
            case DAY:   model.setViewDate(model.nextDay(d));   break;
            case WEEK:  model.setViewDate(model.nextWeek(d));  break;
            case MONTH: model.setViewDate(model.nextMonth(d)); break;
            case YEAR:  model.setViewDate(model.nextYear(d));  break;
        }
    }

    public void loadFromDisk() throws StorageException {
        model.loadEvents(storage.load());
    }

    private void save() throws StorageException {
        storage.save(model.getAllEvents());
    }
}
