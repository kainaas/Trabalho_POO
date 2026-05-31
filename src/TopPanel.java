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

import javax.swing.ImageIcon;
import java.awt.Image;

//TODO: create a class with constants indicating the colors and images of light mode and dark mode

public class TopPanel extends JPanel {
    
    JToggleButton dayView, weekView, monthView, yearView, darkMode, lightMode;
    JButton createButton, editButton;
    JLabel currentViewLabel;
    JTextField searchBar;
    ButtonGroup chooseView, chooseDarkMode;

    ImageIcon dark, light, edit, create, search;

    public TopPanel() {
        super();
        setLayout(new BorderLayout(40, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        createComponents();
        buildLayout();
    }

    private void createComponents() {
        this.dayView = new JToggleButton("Day", true); //starts viewing day
        this.weekView = new JToggleButton("Week", false);
        this.monthView = new JToggleButton("Month", false);
        this.yearView = new JToggleButton("Year", false);
        
        this.chooseView = new ButtonGroup();
        this.chooseView.add(dayView);
        this.chooseView.add(weekView);
        this.chooseView.add(monthView);
        this.chooseView.add(yearView);

        this.dark = new ImageIcon("../imgs/lightMode/darkMode.png"); //TODO: put the right paths.
        this.light = new ImageIcon("../imgs/lightMode/lightMode.png");
        Image scaledDark = this.dark.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        Image scaledLight = this.light.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        this.dark = new ImageIcon(scaledDark);
        this.light = new ImageIcon(scaledLight);
        
        this.darkMode = new JToggleButton(this.dark, true);
        this.lightMode = new JToggleButton(this.light, false);
        //this.darkMode.setBackground(getBackground());

        this.chooseDarkMode = new ButtonGroup();
        this.chooseDarkMode.add(this.darkMode);
        this.chooseDarkMode.add(this.lightMode);

        this.edit = new ImageIcon("../imgs/lightMode/editIcon.png");
        this.create = new ImageIcon("../imgs/lightMode/createIcon.png");
        Image scaledEdit = this.edit.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        Image scaledCreate = this.create.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        this.edit = new ImageIcon(scaledEdit);
        this.create = new ImageIcon(scaledCreate);

        this.createButton = new JButton(edit);
        this.editButton = new JButton(create);
         currentViewLabel = new JLabel(
                "Day of Month of Year",
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
                new FlowLayout(FlowLayout.LEFT, 0, 0));

        leftPanel.add(dayView);
        leftPanel.add(weekView);
        leftPanel.add(monthView);
        leftPanel.add(yearView);

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

   public static void main(String[] args) {
        JFrame frame = new JFrame();
        TopPanel painel = new TopPanel();
        frame.setSize(1920, 1080);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(painel, BorderLayout.NORTH);
        frame.setVisible(true);
    }
}
