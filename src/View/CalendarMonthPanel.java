package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.YearMonth;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import Controller.Controller;
import Model.CalendarModel;

/**
 * Grade do mes inteiro. Cada quadradinho e um dia: da para clicar para
 * selecionar o dia, os dias com eventos ficam destacados e o dia de hoje
 * tambem. Sempre mostra o mes da data atual do modelo.
 */
public class CalendarMonthPanel extends JPanel {
    private static final String[] WEEK_DAYS = { "Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab" };

    private final Controller controller;
    private final CalendarModel model;
    private final JPanel header;
    private final JPanel grid;

    public CalendarMonthPanel(Controller controller) {
        this.controller = controller;
        this.model = controller.getModel();

        setLayout(new BorderLayout());
        header = new JPanel(new GridLayout(1, 7));
        grid = new JPanel(new GridLayout(0, 7, 2, 2));
        grid.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        add(header, BorderLayout.NORTH);
        add(grid, BorderLayout.CENTER);

        refresh();
    }

    public void refresh() {
        modeColors mc = modeColors.of(model.getDarkMode());
        setBackground(mc.background);
        header.setBackground(mc.background);
        grid.setBackground(mc.background);

        buildHeader(mc);
        buildGrid(mc);

        revalidate();
        repaint();
    }

    private void buildHeader(modeColors mc) {
        header.removeAll();
        for (String dia : WEEK_DAYS) {
            JLabel label = new JLabel(dia, SwingConstants.CENTER);
            label.setFont(new Font("SansSerif", Font.BOLD, 14));
            label.setForeground(mc.subtleText);
            header.add(label);
        }
    }

    private void buildGrid(modeColors mc) {
        grid.removeAll();

        LocalDate viewDate = model.getCurrentViewDate();
        YearMonth month = YearMonth.from(viewDate);
        LocalDate firstDay = month.atDay(1);

        // quantos espacos em branco antes do dia 1 (domingo como primeira coluna)
        int leading = firstDay.getDayOfWeek().getValue() % 7;
        for (int i = 0; i < leading; i++) {
            JPanel empty = new JPanel();
            empty.setBackground(mc.background);
            grid.add(empty);
        }

        LocalDate today = LocalDate.now();
        for (int dia = 1; dia <= month.lengthOfMonth(); dia++) {
            LocalDate date = month.atDay(dia);
            grid.add(makeDayCell(date, today, viewDate, mc));
        }
    }

    private JButton makeDayCell(LocalDate date, LocalDate today, LocalDate selected, modeColors mc) {
        JButton cell = new JButton(String.valueOf(date.getDayOfMonth()));
        cell.setVerticalAlignment(SwingConstants.TOP);
        cell.setHorizontalAlignment(SwingConstants.LEFT);
        cell.setFocusPainted(false);
        cell.setForeground(mc.text);

        Color fundo = mc.panel;
        if (model.hasEventsOn(date)) {
            fundo = mc.hasEvents;
        }
        if (date.equals(today)) {
            fundo = mc.today;
        }
        if (date.equals(selected)) {
            fundo = mc.selected;
        }
        cell.setBackground(fundo);
        cell.setBorder(BorderFactory.createLineBorder(mc.gridLine));

        cell.addActionListener(e -> controller.selectDate(date));
        return cell;
    }
}
