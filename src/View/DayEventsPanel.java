package View;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import Controller.Controller;
import Model.Attendee;
import Model.CalendarModel;
import Model.Event;
import Model.StorageException;

/**
 * Painel lateral que mostra os eventos do dia selecionado. Da para ver os
 * detalhes de cada evento e usar os botoes para criar, editar ou excluir.
 * Quando o evento se repete, pergunta se a acao vale so para aquele dia ou
 * para a serie inteira.
 */
public class DayEventsPanel extends JPanel {
    private static final int SCOPE_THIS = 0;
    private static final int SCOPE_ALL = 1;
    private static final Locale PT_BR = new Locale("pt", "BR");

    private final JFrame owner;
    private final Controller controller;
    private final CalendarModel model;

    private final JLabel dateLabel;
    private final DefaultListModel<Event> listModel;
    private final JList<Event> eventJList;
    private final JTextArea details;

    public DayEventsPanel(JFrame owner, Controller controller) {
        this.owner = owner;
        this.controller = controller;
        this.model = controller.getModel();

        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        dateLabel = new JLabel("", SwingConstants.CENTER);
        dateLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        listModel = new DefaultListModel<>();
        eventJList = new JList<>(listModel);
        eventJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventJList.setCellRenderer(new EventRenderer());
        eventJList.addListSelectionListener(e -> showDetails());

        details = new JTextArea(8, 22);
        details.setEditable(false);
        details.setLineWrap(true);
        details.setWrapStyleWord(true);

        JScrollPane listScroll = new JScrollPane(eventJList);
        JScrollPane detailScroll = new JScrollPane(details);

        JPanel center = new JPanel(new GridLayout(2, 1, 0, 8));
        center.add(listScroll);
        center.add(detailScroll);

        add(dateLabel, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        refresh();
    }

    private JPanel buildButtons() {
        JButton novo = new JButton("Novo");
        JButton editar = new JButton("Editar");
        JButton excluir = new JButton("Excluir");

        novo.addActionListener(e -> openCreate());
        editar.addActionListener(e -> openEdit());
        excluir.addActionListener(e -> doDelete());

        JPanel panel = new JPanel(new GridLayout(1, 3, 6, 0));
        panel.add(novo);
        panel.add(editar);
        panel.add(excluir);
        return panel;
    }

    public void refresh() {
        modeColors mc = modeColors.of(model.getDarkMode());
        setBackground(mc.background);
        dateLabel.setForeground(mc.text);
        eventJList.setBackground(mc.panel);
        eventJList.setForeground(mc.text);
        details.setBackground(mc.panel);
        details.setForeground(mc.text);

        LocalDate day = model.getCurrentViewDate();
        dateLabel.setText(formatDate(day));

        listModel.clear();
        List<Event> doDia = model.getEventsOn(day);
        for (Event e : doDia) {
            listModel.addElement(e);
        }
        details.setText("");

        revalidate();
        repaint();
    }

    private void showDetails() {
        Event e = eventJList.getSelectedValue();
        if (e == null) {
            details.setText("");
            return;
        }

        LocalDate day = model.getCurrentViewDate();
        StringBuilder sb = new StringBuilder();
        sb.append("Titulo: ").append(e.getTitle()).append('\n');
        sb.append("Quando: ").append(e.getTime())
          .append(" (").append(e.getDuration()).append(" min)\n");
        sb.append("Categoria: ").append(e.getCategory()).append('\n');
        if (e.getLocation() != null && !e.getLocation().isEmpty()) {
            sb.append("Local: ").append(e.getLocation()).append('\n');
        }
        sb.append("Repeticao: ").append(e.getRecurrence()).append('\n');
        sb.append("Lembrete: ").append(reminderText(e.getReminderLeadMinutes())).append('\n');

        if (e.getDescription() != null && !e.getDescription().isEmpty()) {
            sb.append("\nDescricao:\n").append(e.getDescription()).append('\n');
        }
        if (!e.getAttendees().isEmpty()) {
            sb.append("\nParticipantes:\n");
            for (Attendee a : e.getAttendees()) {
                sb.append("- ").append(a).append('\n');
            }
        }
        details.setText(sb.toString());
        details.setCaretPosition(0);
    }

    public void openCreate() {
        EventFormDialog dialog = new EventFormDialog(owner, controller, null, model.getCurrentViewDate());
        dialog.setVisible(true);
        Event novo = dialog.getResult();
        if (novo == null) {
            return;
        }
        try {
            boolean ok = controller.createEvent(novo);
            if (!ok) {
                JOptionPane.showMessageDialog(this,
                    "Ja existe um evento com esse titulo.",
                    "Nao foi possivel criar", JOptionPane.WARNING_MESSAGE);
            }
        } catch (StorageException ex) {
            showStorageError(ex);
        }
    }

    public void openEdit() {
        Event selecionado = eventJList.getSelectedValue();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this,
                "Selecione um evento para editar.",
                "Nenhum evento selecionado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        LocalDate day = model.getCurrentViewDate();
        EventFormDialog dialog = new EventFormDialog(owner, controller, selecionado, day);
        dialog.setVisible(true);
        Event editado = dialog.getResult();
        if (editado == null) {
            return;
        }

        try {
            if (selecionado.isRecurring()) {
                int escopo = askScope("editar");
                if (escopo == SCOPE_THIS) {
                    controller.editOccurrence(selecionado, day, editado);
                } else if (escopo == SCOPE_ALL) {
                    replaceWarningIfNeeded(controller.replaceEvent(selecionado, editado));
                }
            } else {
                replaceWarningIfNeeded(controller.replaceEvent(selecionado, editado));
            }
        } catch (StorageException ex) {
            showStorageError(ex);
        }
    }

    private void doDelete() {
        Event selecionado = eventJList.getSelectedValue();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this,
                "Selecione um evento para excluir.",
                "Nenhum evento selecionado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        LocalDate day = model.getCurrentViewDate();
        try {
            if (selecionado.isRecurring()) {
                int escopo = askScope("excluir");
                if (escopo == SCOPE_THIS) {
                    controller.deleteOccurrence(selecionado, day);
                } else if (escopo == SCOPE_ALL) {
                    controller.deleteEvent(selecionado);
                }
            } else {
                int r = JOptionPane.showConfirmDialog(this,
                    "Excluir o evento \"" + selecionado.getTitle() + "\"?",
                    "Confirmar exclusao", JOptionPane.YES_NO_OPTION);
                if (r == JOptionPane.YES_OPTION) {
                    controller.deleteEvent(selecionado);
                }
            }
        } catch (StorageException ex) {
            showStorageError(ex);
        }
    }

    // pergunta se a acao vale so para o dia escolhido ou para a serie toda
    private int askScope(String acao) {
        Object[] opcoes = { "Somente esta", "Toda a serie", "Cancelar" };
        return JOptionPane.showOptionDialog(this,
            "Este evento se repete. Deseja " + acao + " somente esta ocorrencia ou a serie inteira?",
            "Evento recorrente",
            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, opcoes, opcoes[0]);
    }

    private void replaceWarningIfNeeded(boolean ok) {
        if (!ok) {
            JOptionPane.showMessageDialog(this,
                "Ja existe outro evento com esse titulo.",
                "Nao foi possivel salvar", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showStorageError(StorageException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(),
            "Erro de arquivo", JOptionPane.ERROR_MESSAGE);
    }

    private String reminderText(long minutes) {
        if (minutes <= 0)   return "Sem lembrete";
        if (minutes == 10)  return "10 minutos antes";
        if (minutes == 60)  return "1 hora antes";
        if (minutes == 1440) return "1 dia antes";
        return minutes + " minutos antes";
    }

    private String formatDate(LocalDate day) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", PT_BR);
        String texto = day.format(fmt);
        // deixa a primeira letra maiuscula (o nome do dia vem em minusculo)
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }

    // mostra cada evento da lista como "HH:mm  Titulo"
    private class EventRenderer extends JLabel implements ListCellRenderer<Event> {
        EventRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Event> list, Event value,
                int index, boolean isSelected, boolean cellHasFocus) {
            modeColors mc = modeColors.of(model.getDarkMode());
            setText(value.getTime() + "  " + value.getTitle());
            if (isSelected) {
                setBackground(mc.selected);
                setForeground(mc.text);
            } else {
                setBackground(mc.panel);
                setForeground(mc.text);
            }
            return this;
        }
    }
}
