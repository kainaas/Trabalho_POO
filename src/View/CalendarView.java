package View;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import Controller.Controller;
import Model.CalendarModel;
import Model.Event;

/**
 * Janela principal do programa. Junta o painel de cima (botoes e busca),
 * a grade do mes e a lista de eventos do dia. Ouve as mudancas do modelo
 * (padrao Observer dos JavaBeans) e manda os paineis se atualizarem.
 */
public class CalendarView extends JFrame implements PropertyChangeListener {
    private final Controller controller;
    private final CalendarModel model;

    private final TopPanel topPanel;
    private final CalendarMonthPanel monthPanel;
    private final DayEventsPanel dayPanel;

    public CalendarView(Controller controller) {
        this.controller = controller;
        this.model = controller.getModel();

        setTitle("Java Event Planner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 680);
        setMinimumSize(new Dimension(900, 560));
        setLocationRelativeTo(null);

        topPanel = new TopPanel();
        topPanel.bind(controller);
        monthPanel = new CalendarMonthPanel(controller);
        dayPanel = new DayEventsPanel(this, controller);
        dayPanel.setPreferredSize(new Dimension(330, 0));

        add(topPanel, BorderLayout.NORTH);
        add(monthPanel, BorderLayout.CENTER);
        add(dayPanel, BorderLayout.EAST);

        wireExtraButtons();
        model.addPropertyChangeListener(this);
        refreshAll();
    }

    // botoes e busca que dependem de outros paineis sao ligados aqui
    private void wireExtraButtons() {
        topPanel.getCreateButton().addActionListener(e -> dayPanel.openCreate());
        topPanel.getEditButton().addActionListener(e -> dayPanel.openEdit());
        topPanel.getSearchBar().addActionListener(e -> doSearch());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // qualquer mudanca relevante no modelo redesenha os paineis
        switch (evt.getPropertyName()) {
            case "currentMode":
            case "currentViewDate":
            case "darkMode":
            case "eventAdded":
            case "eventRemoved":
                refreshAll();
                break;
        }
    }

    private void refreshAll() {
        getContentPane().setBackground(modeColors.of(model.getDarkMode()).background);
        topPanel.refresh();
        monthPanel.refresh();
        dayPanel.refresh();
    }

    // busca por palavra-chave em todos os eventos e deixa o usuario ir ate a data
    private void doSearch() {
        String keyword = topPanel.getSearchBar().getText();
        if (keyword.trim().isEmpty()) {
            return;
        }

        List<Event> results = controller.search(keyword);
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Nenhum evento encontrado para \"" + keyword.trim() + "\".",
                "Busca", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String[] linhas = new String[results.size()];
        for (int i = 0; i < results.size(); i++) {
            Event e = results.get(i);
            linhas[i] = e.getDate().format(fmt) + " " + e.getTime() + " - " + e.getTitle();
        }

        JList<String> lista = new JList<>(linhas);
        lista.setSelectedIndex(0);
        JScrollPane scroll = new JScrollPane(lista);
        scroll.setPreferredSize(new Dimension(360, 200));

        int r = JOptionPane.showConfirmDialog(this, scroll,
            "Resultados da busca (selecione para ir ate a data)",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        int idx = lista.getSelectedIndex();
        if (r == JOptionPane.OK_OPTION && idx >= 0) {
            controller.selectDate(results.get(idx).getDate());
        }
    }
}
