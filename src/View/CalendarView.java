package View;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import Controller.Controller;
import Model.CalendarModel;
import Model.Event;

/**
 * Main window of the application. It combines the top bar, the central calendar
 * view (which switches between day, week, month and year) and the day-events side
 * panel. It listens to the model (JavaBeans Observer pattern) and tells the
 * panels to refresh.
 */
public class CalendarView extends JFrame implements PropertyChangeListener {
    private final Controller controller;
    private final CalendarModel model;

    private final TopPanel topPanel;
    private final DayEventsPanel dayPanel;

    /** Central views, one per mode; the active one is shown in the center. */
    private final Map<ViewMode, CalendarSubView> views = new EnumMap<>(ViewMode.class);
    private final JPanel centerArea;

    /**
     * Builds and lays out the main window.
     *
     * @param controller the controller that drives the application
     */
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
        dayPanel = new DayEventsPanel(this, controller);
        dayPanel.setPreferredSize(new Dimension(330, 0));

        views.put(ViewMode.DAY, new DayViewPanel(controller));
        views.put(ViewMode.WEEK, new WeekViewPanel(controller));
        views.put(ViewMode.MONTH, new CalendarMonthPanel(controller));
        views.put(ViewMode.YEAR, new YearViewPanel(controller));

        centerArea = new JPanel(new BorderLayout());

        add(topPanel, BorderLayout.NORTH);
        add(centerArea, BorderLayout.CENTER);
        add(dayPanel, BorderLayout.EAST);

        wireExtraButtons();
        model.addPropertyChangeListener(this);
        refreshAll();
    }

    /** Wires the buttons and search field that depend on other panels. */
    private void wireExtraButtons() {
        topPanel.getCreateButton().addActionListener(e -> dayPanel.openCreate());
        topPanel.getEditButton().addActionListener(e -> dayPanel.openEdit());
        topPanel.getSearchBar().addActionListener(e -> doSearch());
    }

    /**
     * Reacts to relevant model changes by redrawing the panels.
     *
     * @param evt the property-change event
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
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

    /** Refreshes the top bar, the active central view and the side panel. */
    private void refreshAll() {
        ThemeColors mc = ThemeColors.of(model.getDarkMode());
        getContentPane().setBackground(mc.background);
        centerArea.setBackground(mc.background);

        topPanel.refresh();
        showActiveView();
        dayPanel.refresh();
    }

    /** Places the central view that matches the model's current mode. */
    private void showActiveView() {
        CalendarSubView view = views.get(model.getCurrentMode());
        view.refresh();
        centerArea.removeAll();
        centerArea.add(view.getComponent(), BorderLayout.CENTER);
        centerArea.revalidate();
        centerArea.repaint();
    }

    /**
     * Searches every event for the keyword in the search bar and lets the user
     * jump to one of the results.
     */
    private void doSearch() {
        String keyword = topPanel.getSearchBar().getText();
        if (keyword.trim().isEmpty()) {
            return;
        }

        List<Event> results = controller.search(keyword);
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No event found for \"" + keyword.trim() + "\".",
                "Search", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String[] lines = new String[results.size()];
        for (int i = 0; i < results.size(); i++) {
            Event e = results.get(i);
            lines[i] = e.getDate().format(fmt) + " " + e.getTime() + " - " + e.getTitle();
        }

        JList<String> list = new JList<>(lines);
        list.setSelectedIndex(0);
        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(360, 200));

        int r = JOptionPane.showConfirmDialog(this, scroll,
            "Search results (select one to jump to its date)",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        int idx = list.getSelectedIndex();
        if (r == JOptionPane.OK_OPTION && idx >= 0) {
            controller.selectDate(results.get(idx).getDate());
        }
    }
}
