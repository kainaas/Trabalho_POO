package View;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import Controller.Controller;
import Model.CalendarModel;

import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Image;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

//TODO: create a class with constants indicating the colors and images of light mode and dark mode


/**
 * Top panel which holds the buttons and search bar that interacts with the view and events
 */
public class TopPanel extends JPanel {
    
    JToggleButton dayView, weekView, monthView, yearView, darkMode, lightMode;
    JButton createButton, editButton, previousDate, nextDate;
    JLabel currentViewLabel;
    JTextField searchBar;
    ButtonGroup chooseView, chooseDarkMode;

    ImageIcon darkSwitch, lightSwitch, edit, create, search;

    Controller Control;

    public TopPanel() {
        super();
        setLayout(new BorderLayout(40, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));
        createComponents();
        buildLayout();

        //TODO: make a function that refresh the icons and colors of buttons and colors
    }

    private void createComponents() {
        Dimension viewButtonSize = new Dimension(90, 35);

        this.dayView = new JToggleButton("Day", false);
        this.weekView = new JToggleButton("Week", false);
        this.monthView = new JToggleButton("Month", true); //comeca mostrando o mes
        this.yearView = new JToggleButton("Year", false);

        dayView.setPreferredSize(viewButtonSize);
        weekView.setPreferredSize(viewButtonSize);
        monthView.setPreferredSize(viewButtonSize);
        yearView.setPreferredSize(viewButtonSize);
        
        this.chooseView = new ButtonGroup();
        this.chooseView.add(dayView);
        this.chooseView.add(weekView);
        this.chooseView.add(monthView);
        this.chooseView.add(yearView);

        this.darkSwitch = Icons.lightIcon(IconFiles.SWITCH_DARK, 30, 30);
        this.lightSwitch = Icons.lightIcon(IconFiles.SWITCH_LIGHT, 30, 30);

        this.lightMode = new JToggleButton(this.lightSwitch);
        this.darkMode = new JToggleButton(this.darkSwitch);

        this.chooseDarkMode = new ButtonGroup();
        this.chooseDarkMode.add(this.darkMode);
        this.chooseDarkMode.add(this.lightMode);

        this.edit = Icons.lightIcon(IconFiles.EDIT_ICON, 20, 20);
        this.create = Icons.lightIcon(IconFiles.CREATE_ICON, 20, 20);

        this.createButton = new JButton(create);
        this.editButton = new JButton(edit);

        this.previousDate = new JButton("<");
        this.nextDate = new JButton(">");
        previousDate.setPreferredSize(viewButtonSize);
        nextDate.setPreferredSize(viewButtonSize);

        String label = LocalDate.now().toString();
        this.currentViewLabel = new JLabel(
                label,
                SwingConstants.CENTER);

        currentViewLabel.setFont(
                new Font("SansSerif", Font.BOLD, 24));

        searchBar = new JTextField();
        searchBar.setPreferredSize(new Dimension(350, 40));
        searchBar.setToolTipText(
                "Search by title, description or location");
    }


    private void buildLayout() {

        // ==========================================
        // LEFT PANEL (Day / Week / Month / Year)
        // ==========================================
        JPanel leftPanel = new JPanel(
            new GridLayout(2,1));

        JPanel viewModePanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 0, 0));
        viewModePanel.add(dayView);
        viewModePanel.add(weekView);
        viewModePanel.add(monthView);
        viewModePanel.add(yearView);

        JPanel previousNextPanel = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 0, 0));
        previousNextPanel.add(previousDate);
        previousNextPanel.add(nextDate);
        leftPanel.add(viewModePanel);
        leftPanel.add(previousNextPanel);

        // ==========================================
        // CENTER PANEL
        // ==========================================
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));

        centerPanel.add(currentViewLabel, BorderLayout.NORTH);
        centerPanel.add(searchBar, BorderLayout.CENTER);

        // ==========================================
        // RIGHT PANEL
        // ==========================================
        JPanel rightPanel = new JPanel(
                new GridLayout(2, 2, 10, 10));

        rightPanel.add(lightMode);
        rightPanel.add(darkMode);
        rightPanel.add(editButton);
        rightPanel.add(createButton);

        // ==========================================
        // ADD TO TOP PANEL
        // ==========================================
        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }


    private void refreshLabel() {

    }

    // liga os botoes do topo as acoes do controller
    public void bind(Controller controller) {
        this.Control = controller;

        dayView.addActionListener(e -> controller.setViewMode(ViewMode.DAY));
        weekView.addActionListener(e -> controller.setViewMode(ViewMode.WEEK));
        monthView.addActionListener(e -> controller.setViewMode(ViewMode.MONTH));
        yearView.addActionListener(e -> controller.setViewMode(ViewMode.YEAR));

        previousDate.addActionListener(e -> controller.goPrevious());
        nextDate.addActionListener(e -> controller.goNext());

        darkMode.addActionListener(e -> controller.setDarkMode(true));
        lightMode.addActionListener(e -> controller.setDarkMode(false));
    }

    // atualiza o rotulo central e as cores conforme o estado do modelo
    public void refresh() {
        if (Control == null) {
            return;
        }
        CalendarModel model = Control.getModel();
        currentViewLabel.setText(buildLabel(model));
        applyTheme(modeColors.of(model.getDarkMode()));
    }

    public JButton getCreateButton() {
        return createButton;
    }

    public JButton getEditButton() {
        return editButton;
    }

    public JTextField getSearchBar() {
        return searchBar;
    }

    private String buildLabel(CalendarModel model) {
        LocalDate d = model.getCurrentViewDate();
        Locale ptbr = new Locale("pt", "BR");
        switch (model.getCurrentMode()) {
            case DAY:
                return capitalize(d.format(
                    DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", ptbr)));
            case WEEK:
                return "Semana de " + d.format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            case YEAR:
                return String.valueOf(d.getYear());
            case MONTH:
            default:
                return capitalize(d.format(
                    DateTimeFormatter.ofPattern("MMMM 'de' yyyy", ptbr)));
        }
    }

    private void applyTheme(modeColors mc) {
        paintPanels(this, mc.background);
        currentViewLabel.setForeground(mc.text);
        searchBar.setBackground(mc.panel);
        searchBar.setForeground(mc.text);
    }

    // pinta o fundo deste painel e dos paineis internos para o tema combinar
    private void paintPanels(Container container, Color bg) {
        if (container instanceof JPanel) {
            container.setBackground(bg);
        }
        for (Component comp : container.getComponents()) {
            if (comp instanceof Container) {
                paintPanels((Container) comp, bg);
            }
        }
    }

    private String capitalize(String s) {
        if (s.isEmpty()) {
            return s;
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}

